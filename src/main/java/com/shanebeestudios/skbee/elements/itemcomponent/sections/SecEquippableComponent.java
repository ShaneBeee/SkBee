package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SecEquippableComponent extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        @SuppressWarnings("unchecked")
        Class<Object>[] classes = (Class<Object>[]) CollectionUtils.array(EntityData.class, EntityType.class, Tag.class, TagKey.class, RegistryKeySet.class);

        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("slot", EquipmentSlot.class)
            .addOptionalEntry("equip_sound", String.class)
            .addOptionalEntry("asset_id", String.class)
            .addOptionalEntry("allowed_entities", classes)
            .addOptionalEntry("dispensable", Boolean.class)
            .addOptionalEntry("swappable", Boolean.class)
            .addOptionalEntry("damage_on_hurt", Boolean.class)
            .addOptionalEntry("camera_overlay", String.class)
            .build();
        reg.newSection(SecEquippableComponent.class, VALIDATOR,
            "apply equippable [component] to %itemstacks/itemtypes/slots%")
            .name("ItemComponent - Equippable Component Apply")
            .description("Apply an equippable component to any item so it can be equipped in the specified slot.",
                "Requires Paper 1.21.3+",
                "See [**Equippable Component**](https://minecraft.wiki/w/Data_component_format#equippable) on McWiki for more info.",
                "",
                "**Entries**:",
                "- `slot` = The slot the item can be put on (See Equipment Slot).",
                "- `equip_sound` = The sound to be played when equipped. [Optional]",
                "- `asset_id` = The key of the equipment model to use when equipped. [Optional]",
                "- `allowed_entities` = A list of entity types or a Minecraft entity tag that can equip this item. [Optional]",
                "- `dispensable` = Whether the item can be dispensed by using a dispenser. Defaults to true. [Optional]",
                "- `swappable` = Whether the item can be equipped into the relevant slot by right-clicking. Defaults to true. [Optional]",
                "- `damage_on_hurt` = Whether this item is damaged when the wearing entity is damaged. Defaults to true. [Optional]",
                "- `camera_overlay` = The key of the overlay texture to use when equipped. [Optional]")
            .examples("apply equippable component to {_item}:",
                "\tslot: hand_slot",
                "\tequip_sound: \"entity.player.burp\"",
                "\tasset_id: \"my_pack:some_asset\"",
                "\tallowed_entities: player, evoker, zombie # Shown as list of entity types.",
                "\tallowed_entities: minecraft entity tag \"undead\" # Shown as Minecraft entity tag",
                "\tdispensable: false",
                "\tswappable: true",
                "\tdamage_on_hurt: true",
                "\tcamera_overlay: \"my_pack:some_overlay\"")
            .since("3.8.0")
            .register();
    }

    private Expression<Object> items;
    private Expression<EquipmentSlot> equipmentSlot;
    private Expression<String> equipSound;
    private Expression<String> assetId;
    private Expression<?> allowedEntities;
    private Expression<Boolean> dispensable;
    private Expression<Boolean> swappable;
    private Expression<Boolean> damageOnHurt;
    private Expression<String> cameraOverlay;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.items = (Expression<Object>) exprs[0];
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.equipmentSlot = (Expression<EquipmentSlot>) container.getOptional("slot", false);
        this.equipSound = (Expression<String>) container.getOptional("equip_sound", false);
        this.assetId = (Expression<String>) container.getOptional("asset_id", false);
        this.allowedEntities = (Expression<?>) container.getOptional("allowed_entities", false);
        this.dispensable = (Expression<Boolean>) container.getOptional("dispensable", false);
        this.swappable = (Expression<Boolean>) container.getOptional("swappable", false);
        this.damageOnHurt = (Expression<Boolean>) container.getOptional("damage_on_hurt", false);
        this.cameraOverlay = (Expression<String>) container.getOptional("camera_overlay", false);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (this.equipmentSlot == null) return null;

        EquipmentSlot slot = this.equipmentSlot.getOptionalSingle(event).orElse(EquipmentSlot.HEAD);
        Equippable.Builder builder = Equippable.equippable(slot);
        if (this.equipSound != null) {
            Key key = KeyUtils.getKey(this.equipSound.getOptionalSingle(event).orElse("item.armor.equip_generic"));
            if (key != null) builder.equipSound(key);
        }
        if (this.assetId != null) {
            String assetId = this.assetId.getSingle(event);
            if (assetId != null) {
                Key key = KeyUtils.getKey(assetId);
                if (key != null) builder.assetId(key);
            }
        }

        List<TypedKey<EntityType>> typedKeys = new ArrayList<>();
        if (this.allowedEntities != null) {
            for (Object object : this.allowedEntities.getArray(event)) {
                if (object instanceof EntityData<?> entityData) {
                    EntityType bukkitEntityType = EntityUtils.toBukkitEntityType(entityData);
                    TypedKey<EntityType> typedKey = TypedKey.create(RegistryKey.ENTITY_TYPE, bukkitEntityType.key());
                    if (!typedKeys.contains(typedKey)) typedKeys.add(typedKey);
                } else if (object instanceof EntityType entityType) {
                    TypedKey<EntityType> typedKey = TypedKey.create(RegistryKey.ENTITY_TYPE, entityType.key());
                    if (!typedKeys.contains(typedKey)) typedKeys.add(typedKey);
                } else if (object instanceof Tag<?> tag) {
                    RegistryKeySet<EntityType> keySet = RegistryUtils.getKeySet(tag, RegistryKey.ENTITY_TYPE);
                    builder.allowedEntities(keySet);
                    // Clear the keys in the event we have a tag
                    // We can't have both (either a list of entity types OR 1 tag)
                    typedKeys.clear();
                    break;
                } else if (object instanceof TagKey<?> tagKey && tagKey.registryKey() == RegistryKey.ENTITY_TYPE) {
                    @SuppressWarnings("unchecked")
                    TagKey<EntityType> entityTagKey = (TagKey<EntityType>) tagKey;
                    builder.allowedEntities(RegistryUtils.getRegistry(RegistryKey.ENTITY_TYPE).getTag(entityTagKey));
                    // See above
                    typedKeys.clear();
                    break;
                } else if (object instanceof RegistryKeySet<?> keySet && keySet.registryKey() == RegistryKey.ENTITY_TYPE) {
                    @SuppressWarnings("unchecked")
                    RegistryKeySet<EntityType> entityKeySet = (RegistryKeySet<EntityType>) keySet;
                    builder.allowedEntities(entityKeySet);
                    // See above
                    typedKeys.clear();
                    break;
                }
            }
        }

        if (!typedKeys.isEmpty()) {
            RegistryKeySet<EntityType> keySet = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, typedKeys);
            builder.allowedEntities(keySet);
        }
        if (this.dispensable != null) {
            boolean dispensable = this.dispensable.getOptionalSingle(event).orElse(true);
            builder.dispensable(dispensable);
        }
        if (this.swappable != null) {
            boolean swappable = this.swappable.getOptionalSingle(event).orElse(true);
            builder.swappable(swappable);
        }
        if (this.damageOnHurt != null) {
            Boolean damageOnHurt = this.damageOnHurt.getOptionalSingle(event).orElse(true);
            builder.damageOnHurt(damageOnHurt);
        }
        if (this.cameraOverlay != null) {
            String overlay = this.cameraOverlay.getSingle(event);
            if (overlay != null) {
                Key key = KeyUtils.getKey(overlay);
                if (key != null) builder.cameraOverlay(key);
            }
        }

        Equippable equippable = builder.build();
        ItemUtils.modifyItems(this.items.getArray(event), itemStack -> {
            itemStack.setData(DataComponentTypes.EQUIPPABLE, equippable);
        });
        return super.walk(event, false);
    }

    @Override
    public String toString(Event e, boolean d) {
        return "apply equippable component to " + this.items.toString(e, d);
    }

}
