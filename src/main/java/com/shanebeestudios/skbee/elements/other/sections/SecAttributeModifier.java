package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

public class SecAttributeModifier extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        builder.addRequiredEntry("attribute", Attribute.class);
        builder.addOptionalEntry("slot", EquipmentSlotGroup.class);
        builder.addRequiredEntry("id", String.class);
        builder.addRequiredEntry("amount", Number.class);
        builder.addRequiredEntry("operation", Operation.class);
        VALIDATOR = builder.build();
        reg.newSection(SecAttributeModifier.class, VALIDATOR,
                "apply [:transient] attribute modifier to %itemtypes/livingentities%")
            .name("Attribute Modifier - Apply")
            .description("Apply an attribute modifier to an item or living entity.",
                "See [**McWiki Component**](https://minecraft.wiki/w/Data_component_format#attribute_modifiers) and " +
                    "[**McWiki Modifiers**](https://minecraft.wiki/w/Attribute#Modifiers) for further details.",
                "",
                "`transient` = Non-persisent attribute modifier (LivingEntities only, not Items), will not save to the entity's NBT (Requires PaperMC).",
                "",
                "**Entries/Sections**:",
                "- `attribute` = The attribute this modifier is to act upon.",
                "- `slot` = EquipmentSlotGroup the item must be in for the modifier to take effect (optional, default = any).",
                "- `id` = The NamespacedKey to identify this modifier.",
                "- `amount` = Amount of change from the modifier.",
                "- `operation` = The operation to decide how to modify.")
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
    private Expression<EquipmentSlotGroup> slot;
    private Expression<String> id;
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
        this.slot = (Expression<EquipmentSlotGroup>) container.getOptional("slot", false);
        this.id = (Expression<String>) container.getOptional("id", false);
        if (this.id == null) return false;

        this.amount = (Expression<Number>) container.getOptional("amount", false);
        this.operation = (Expression<Operation>) container.getOptional("operation", false);
        return this.attribute != null && this.amount != null;
    }

    @SuppressWarnings({"DataFlowIssue"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        Number amountNum = this.amount.getSingle(event);
        Operation operation = this.operation.getSingle(event);

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

        EquipmentSlotGroup slotGroup = this.slot != null ? this.slot.getSingle(event) : null;
        if (slotGroup == null) {
            slotGroup = EquipmentSlotGroup.ANY;
        }
        attributeModifier = new AttributeModifier(namespacedKey, amountNum.doubleValue(), operation, slotGroup);


        for (Object object : this.objects.getArray(event)) {
            if (object instanceof ItemType itemType) {
                ItemMeta itemMeta = itemType.getItemMeta();

                if (ItemUtils.hasAttributeModifier(itemMeta, attribute, attributeModifier)) {
                    // Remove the old modifier so we can override
                    for (AttributeModifier modifier : itemMeta.getAttributeModifiers(attribute)) {
                        if (modifier.getKey().equals(attributeModifier.getKey())) {
                            itemMeta.removeAttributeModifier(attribute, modifier);
                        }
                    }
                }
                itemMeta.addAttributeModifier(attribute, attributeModifier);

                itemType.setItemMeta(itemMeta);
            } else if (object instanceof LivingEntity entity) {
                AttributeInstance attributeInstance = entity.getAttribute(attribute);
                if (attributeInstance == null) continue;

                if (EntityUtils.hasAttributeModifier(entity, attribute, attributeModifier)) {
                    attributeInstance.removeModifier(attributeModifier.key());
                }

                if (this.trans) {
                    attributeInstance.addTransientModifier(attributeModifier);
                } else {
                    attributeInstance.addModifier(attributeModifier);
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
