package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;
import java.util.UUID;

public class SecAttributeModifier extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("attribute", Attribute.class);
        if (ItemUtils.HAS_EQUIPMENT_SLOT_GROUP) {
            builder.addOptionalEntry("slot", EquipmentSlotGroup.class);
        } else {
            builder.addOptionalEntry("slot", EquipmentSlot.class);
        }
        if (ItemUtils.HAS_KEY) {
            builder.addRequiredEntry("id", String.class);
        } else {
            builder.addRequiredEntry("name", String.class);
            builder.addOptionalEntry("uuid", String.class);
        }
        builder.addRequiredEntry("amount", Number.class);
        builder.addRequiredEntry("operation", Operation.class);
        VALIDATOR = builder.build();
        reg.newSection(SecAttributeModifier.class,
                "apply [:transient] attribute modifier to %itemtypes/livingentities%")
            .name("Attribute Modifier - Apply")
            .description("Apply an attribute modifier to an item or living entity.",
                "If running Minecraft 1.21+ use `id`",
                "If running Minecraft 1.20.6 and below, use `name` and `uuid` (uuid is optional, will default to random).",
                "See [**McWiki Component**](https://minecraft.wiki/w/Data_component_format#attribute_modifiers) and " +
                    "[**McWiki Modifiers**](https://minecraft.wiki/w/Attribute#Modifiers) for further details.",
                "",
                "`transient` = Non-persisent attribute modifier (LivingEntities only, not Items), will not save to the entity's NBT (Requires PaperMC).",
                "",
                "**Entries/Sections**:",
                "- `attribute` = The attribute this modifier is to act upon.",
                "- `slot` = Slot Type the item must be in for the modifier to take effect (Minecraft 1.20.6+ uses Equipment Slot Group, other versions use Equipment Slot).",
                "- `id` = The NamespacedKey to identify this modifier (Minecraft 1.21+).",
                "- `name` = The name used to identifiy this modifier (Minecraft 1.20.6 and below).",
                "- `uuid` = The uuid used to identify this modifier [optional] (Minecraft 1.20.6 and below).",
                "- `amount` = Amount of change from the modifier.",
                "- `operation` = The operation to decide how to modify. See [**McWiki**](https://minecraft.wiki/w/Attribute#Operations) for more details.")
            .examples("#Apply Attribute Modifiers to Items",
                "set {_i} to a stick",
                "apply attribute modifier to {_i}:",
                "\tattribute: scale",
                "\tid: \"minecraft:my_hand_scale\"",
                "\tslot: mainhand_slot_group",
                "\tamount: 2",
                "\toperation: add_number",
                "apply attribute modifier to {_i}:",
                "\tattribute: scale",
                "\tid: \"minecraft:my_hand_scale_off\"",
                "\tslot: offhand_slot_group",
                "\tamount: -0.9",
                "\toperation: add_number",
                "give player 1 of {_i}",
                "#Apply Attribute Modifiers to LivingEntities",
                "apply transient attribute modifier to player:",
                "\tid: \"my_mods:mining\"",
                "\tattribute: player mining efficiency",
                "\tamount: 50",
                "\toperation: add_number",
                "apply transient attribute modifier to player:",
                "\tid: \"my_mods:scale\"",
                "\tattribute: scale",
                "\tamount: -0.5",
                "\toperation: add_number")
            .since("3.5.9")
            .register();
    }

    private boolean trans;
    private Expression<?> objects;
    private Expression<Attribute> attribute;
    private Expression<?> slot;
    private Expression<String> id;
    private Expression<String> name;
    private Expression<String> uuid;
    private Expression<Number> amount;
    private Expression<Operation> operation;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.trans = parseResult.hasTag("transient");
        if (this.trans && !EntityUtils.HAS_TRANSIENT) {
            Skript.error("'transient' requires a PaperMC server.");
            return false;
        }
        this.objects = exprs[0];
        this.attribute = (Expression<Attribute>) container.getOptional("attribute", false);
        this.slot = (Expression<?>) container.getOptional("slot", false);
        if (ItemUtils.HAS_KEY) {
            this.id = (Expression<String>) container.getOptional("id", false);
            if (this.id == null) return false;
        } else {
            this.name = (Expression<String>) container.getOptional("name", false);
            this.uuid = (Expression<String>) container.getOptional("uuid", false);
            if (this.name == null) return false;
        }
        this.amount = (Expression<Number>) container.getOptional("amount", false);
        this.operation = (Expression<Operation>) container.getOptional("operation", false);
        return this.attribute != null && this.amount != null;
    }

    @SuppressWarnings("removal")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        Number amountNum = this.amount.getSingle(event);
        Operation operation = this.operation.getSingle(event);
        Object slot = this.slot != null ? this.slot.getSingle(event) : null;

        if (attribute == null) {
            error("Attribute is missing");
            return super.walk(event, false);
        }
        if (amountNum == null) {
            error("Amount is missing");
            return super.walk(event, false);
        }
        if (operation == null) {
            error("Operation is missing");
            return super.walk(event, false);
        }

        AttributeModifier attributeModifier;
        if (ItemUtils.HAS_KEY) {
            String id = this.id.getSingle(event);
            if (id == null) {
                error("Invalid id: " + this.id.toString(event, true));
                return super.walk(event, false);
            }

            NamespacedKey namespacedKey = Util.getNamespacedKey(id, false);
            if (namespacedKey == null) {
                error("Invalid id: " + id);
                return super.walk(event, false);
            }

            EquipmentSlotGroup slotGroup = slot instanceof EquipmentSlotGroup esg ? esg : EquipmentSlotGroup.ANY;
            attributeModifier = new AttributeModifier(namespacedKey, amountNum.doubleValue(), operation, slotGroup);
        } else {
            String name = this.name.getSingle(event);
            String uuidString = this.uuid != null ? this.uuid.getSingle(event) : null;
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException | NullPointerException ignore) {
                uuid = UUID.randomUUID();
            }
            if (name == null) return super.walk(event, false);

            if (slot instanceof EquipmentSlot equipmentSlot) {
                attributeModifier = new AttributeModifier(uuid, name, amountNum.doubleValue(), operation, equipmentSlot);
            } else if (ItemUtils.HAS_EQUIPMENT_SLOT_GROUP && slot instanceof EquipmentSlotGroup slotGroup) {
                attributeModifier = new AttributeModifier(uuid, name, amountNum.doubleValue(), operation, slotGroup);
            } else {
                attributeModifier = new AttributeModifier(uuid, name, amountNum.doubleValue(), operation);
            }

        }

        for (Object object : this.objects.getArray(event)) {
            if (object instanceof ItemType itemType) {
                ItemMeta itemMeta = itemType.getItemMeta();

                if (!ItemUtils.hasAttributeModifier(itemMeta, attribute, attributeModifier)) {
                    itemMeta.addAttributeModifier(attribute, attributeModifier);
                }

                itemType.setItemMeta(itemMeta);
            } else if (object instanceof LivingEntity entity) {
                AttributeInstance attributeInstance = entity.getAttribute(attribute);
                if (attributeInstance == null) continue;
                if (!EntityUtils.hasAttributeModifier(entity, attribute, attributeModifier)) {
                    if (this.trans) {
                        attributeInstance.addTransientModifier(attributeModifier);
                    } else {
                        attributeInstance.addModifier(attributeModifier);
                    }
                }
            }
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String t = this.trans ? " transient " : " ";
        return "apply" + t + "attribute modifier to " + this.objects.toString(e, d);
    }

}
