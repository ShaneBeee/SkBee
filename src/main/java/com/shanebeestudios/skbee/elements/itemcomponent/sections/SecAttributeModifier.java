package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Name("ItemComponent - Attribute Modifier Apply")
@Description({"Apply an attribute modifier to an item. Requires Minecraft 1.21+",
    "See [**McWiki Component**](https://minecraft.wiki/w/Data_component_format#attribute_modifiers) and " +
        "[**McWiki Modifiers**](https://minecraft.wiki/w/Attribute#Modifiers) for further details.",
    "",
    "**Entries/Sections**:",
    "- `attribute` = The attribute this modifier is to act upon.",
    "- `slot` = Equipment Slot Type the item must be in for the modifier to take effect.",
    "- `id` = The NamespacedKey for this modifier.",
    "- `amount` = Amount of change from the modifier.",
    "- `operation` = The operation to decide how to modify. See [**McWiki**](https://minecraft.wiki/w/Attribute#Operations) for more details."})
@Examples({"set {_i} to a stick",
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
    "give player 1 of {_i}"})
@Since("3.5.8")
public class SecAttributeModifier extends Section {
    private static final EntryValidator.EntryValidatorBuilder VALIDATIOR = EntryValidator.builder();

    static {
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("attribute", null, false, Attribute.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("slot", null, false, EquipmentSlotGroup.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("id", null, false, String.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("amount", null, false, Number.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("operation", null, false, Operation.class));
        Skript.registerSection(SecAttributeModifier.class, "apply attribute modifier to %itemtypes%");
    }

    private Expression<ItemType> items;
    private Expression<Attribute> attribute;
    private Expression<EquipmentSlotGroup> slotGroup;
    private Expression<String> id;
    private Expression<Number> amount;
    private Expression<Operation> operation;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATIOR.build().validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<ItemType>) exprs[0];
        this.attribute = (Expression<Attribute>) container.getOptional("attribute", false);
        this.slotGroup = (Expression<EquipmentSlotGroup>) container.getOptional("slot", false);
        this.id = (Expression<String>) container.getOptional("id", false);
        this.amount = (Expression<Number>) container.getOptional("amount", false);
        this.operation = (Expression<Operation>) container.getOptional("operation", false);
        return this.attribute != null && this.slotGroup != null && this.id != null && this.amount != null;
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Attribute attribute = this.attribute.getSingle(event);
        EquipmentSlotGroup slotGroup = this.slotGroup.getSingle(event);
        String id = this.id.getSingle(event);
        Number amountNum = this.amount.getSingle(event);
        Operation operation = this.operation.getSingle(event);

        if (attribute == null || slotGroup == null || id == null || amount == null || operation == null)
            return super.walk(event, false);

        NamespacedKey namespacedKey = Util.getNamespacedKey(id, false);
        if (namespacedKey == null) return super.walk(event, false);

        AttributeModifier attributeModifier = new AttributeModifier(namespacedKey, amountNum.doubleValue(), operation, slotGroup);

        for (ItemType itemType : this.items.getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();

            if (!ItemUtils.hasAttributeModifier(itemMeta, attributeModifier)) {
                itemMeta.addAttributeModifier(attribute, attributeModifier);
            }

            itemType.setItemMeta(itemMeta);
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply attribute modifier to " + this.items.toString(e, d);
    }

}
