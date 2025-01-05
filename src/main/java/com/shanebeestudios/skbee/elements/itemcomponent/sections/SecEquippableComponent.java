package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@Name("ItemComponent - Equippable Component Apply")
@Description({"Apply an equippable component to any item so it can be equipped in the specified slot.",
    "Requires Paper 1.21.3+",
    "See [**Equippable Component**](https://minecraft.wiki/w/Data_component_format#equippable) on McWiki for more info.",
    "",
    "**Entries**:",
    "- `slot` = The slot the item can be put on (See Equipment Slot).",
    "- `equip_sound` = The sound to be played when equipped. [Optional]",
    "- `asset_id` = The key of the equipment model to use when equipped. [Optional]",
    "- `allowed_entity_types` = Types of entities that can equip this item. [Optional]",
    "- `allowed_entity_tags` = Tags of entities that can equip this item. [Optional]",
    "- `dispensable` = Whether the item can be dispensed by using a dispenser. Defaults to true. [Optional]",
    "- `swappable` = Whether the item can be equipped into the relevant slot by right-clicking. Defaults to true. [Optional]",
    "- `damage_on_hurt` = Whether this item is damaged when the wearing entity is damaged. Defaults to true. [Optional]",
    "- `camera_overlay` = The key of the overlay texture to use when equipped. [Optional]"})
@Examples({"apply equippable component to {_item}:",
    "\tslot: hand_slot",
    "\tequip_sound: \"entity.player.burp\"",
    "\tasset_id: \"my_pack:some_asset\"",
    "\tallowed_entity_types: player, evoker",
    "\tallowed_entity_tags: minecraft entity tag \"undead\"",
    "\tdispensable: false",
    "\tswappable: true",
    "\tdamage_on_hurt: true",
    "\tcamera_overlay: \"my_pack:some_overlay\""})
@Since("INSERT VERSION")
@SuppressWarnings("UnstableApiUsage")
public class SecEquippableComponent extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("slot", EquipmentSlot.class)
            .addOptionalEntry("equip_sound", String.class)
            .addOptionalEntry("asset_id", String.class)
            .addOptionalEntry("allowed_entity_types", EntityData.class)
            .addOptionalEntry("allowed_entity_tags", Tag.class)
            .addOptionalEntry("dispensable", Boolean.class)
            .addOptionalEntry("swappable", Boolean.class)
            .addOptionalEntry("damage_on_hurt", Boolean.class)
            .addOptionalEntry("camera_overlay", String.class)
            .build();
        Skript.registerSection(SecEquippableComponent.class,
            "apply equippable [component] to %itemstacks/itemtypes/slots%");
    }

    private Expression<Object> items;
    private Expression<EquipmentSlot> equipmentSlot;
    private Expression<String> equipSound;
    private Expression<String> assetId;
    private Expression<EntityData<?>> allowedEntityTypes;
    private Expression<Tag<?>> allowedEntityTags;
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
        this.allowedEntityTypes = (Expression<EntityData<?>>) container.getOptional("allowed_entity_types", false);
        this.allowedEntityTags = (Expression<Tag<?>>) container.getOptional("allowed_entity_tags", false);
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
            NamespacedKey namespacedKey = Util.getNamespacedKey(this.equipSound.getOptionalSingle(event).orElse("item.armor.equip_generic"), false);
            if (namespacedKey != null) builder.equipSound(namespacedKey);
        }
        if (this.assetId != null) {
            String assetId = this.assetId.getSingle(event);
            if (assetId != null) {
                NamespacedKey namespacedKey = Util.getNamespacedKey(assetId, false);
                if (namespacedKey != null) builder.assetId(namespacedKey);
            }
        }

        List<TypedKey<EntityType>> typedKeys = new ArrayList<>();
        if (this.allowedEntityTypes != null) {
            for (EntityData<?> entityData : this.allowedEntityTypes.getArray(event)) {
                EntityType bukkitEntityType = EntityUtils.toBukkitEntityType(entityData);
                TypedKey<EntityType> typedKey = TypedKey.create(RegistryKey.ENTITY_TYPE, bukkitEntityType.key());
                if (!typedKeys.contains(typedKey)) typedKeys.add(typedKey);
            }
        }
        if (this.allowedEntityTags != null) {
            for (Tag<?> tag : this.allowedEntityTags.getArray(event)) {
                for (Keyed value : tag.getValues()) {
                    if (value instanceof EntityType entityType) {
                        TypedKey<EntityType> typedKey = TypedKey.create(RegistryKey.ENTITY_TYPE, entityType.key());
                        if (!typedKeys.contains(typedKey)) typedKeys.add(typedKey);
                    }
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
                NamespacedKey namespacedKey = Util.getNamespacedKey(overlay, false);
                if (namespacedKey != null) builder.cameraOverlay(namespacedKey);
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
