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
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Name("ItemComponent - Tool Component Apply")
@Description({"Apply a tool component to any item making it usable tool. Requires Minecraft 1.20.5+",
    "",
    "**Entries/Sections**:",
    "- `default mining speed` = The default mining speed of this tool, used if no rules override it. Defaults to 1.0. [Optional]",
    "- `damage per block` = The amount of durability to remove each time a block is broken with this tool. Must be a non-negative integer.",
    "- `rules:` =  A list of rules for the blocks that this tool has a special behavior with."})
@Examples({"set {_i} to a stick",
    "apply tool component to {_i}:",
    "\tdefault mining speed: 2.3",
    "\tdamage per block: 2",
    "\trules:",
    "\t\tapply tool rule:",
    "\t\t\tblock tag: minecraft block tag \"minecraft:dirt\"",
    "\t\t\tspeed: 1.0",
    "\t\t\tcorrect for drops: true",
    "\t\tapply tool rule:",
    "\t\t\tblock types: granite, stone and andesite",
    "\t\t\tspeed: 0.5",
    "\t\t\tcorrect for drops: false",
    "give {_i} to player"})
@Since("INSERT VERSION")
public class SecToolComponent extends Section {

    public static class ToolComponentApplyRulesEvent extends Event {

        private final ToolComponent component;

        public ToolComponentApplyRulesEvent(ToolComponent component) {
            this.component = component;
        }

        public ToolComponent getComponent() {
            return this.component;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("ToolComponentApplyRulesEvent should never be called");
        }
    }

    private static final EntryValidator.EntryValidatorBuilder VALIDATIOR = EntryValidator.builder();

    static {
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("default mining speed", null, false, Number.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("damage per block", null, true, Number.class));
        VALIDATIOR.addSection("rules", true);
        Skript.registerSection(SecToolComponent.class, "apply tool component to %itemtypes/itemstacks/slots%");
    }

    private Expression<?> items;
    private Expression<Number> defaultMiningSpeed;
    private Expression<Number> damagePerBlock;
    private Trigger rulesSection;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATIOR.build().validate(sectionNode);
        if (container == null) return false;

        this.items = expr[0];
        this.defaultMiningSpeed = (Expression<Number>) container.getOptional("default mining speed", false);
        this.damagePerBlock = (Expression<Number>) container.getOptional("damage per block", false);

        SectionNode rulesNode = container.getOptional("rules", SectionNode.class, false);
        if (rulesNode != null) {
            this.rulesSection = loadCode(rulesNode, "rules section", ToolComponentApplyRulesEvent.class);
        }
        return true;
    }

    @SuppressWarnings({"NullableProblems", "IfCanBeSwitch"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Number miningSpeedNum = this.defaultMiningSpeed.getSingle(event);
        Number damagePerNum = this.damagePerBlock.getSingle(event);
        if (damagePerNum == null) return super.walk(event, false);

        int damagePerBlock = damagePerNum.intValue();

        for (Object object : this.items.getArray(event)) {
            ItemMeta itemMeta;
            if (object instanceof ItemStack itemStack) itemMeta = itemStack.getItemMeta();
            else if (object instanceof ItemType itemType) itemMeta = itemType.getItemMeta();
            else if (object instanceof Slot slot) itemMeta = slot.getItem().getItemMeta();
            else continue;

            ToolComponent tool = itemMeta.getTool();
            if (miningSpeedNum != null) {
                tool.setDefaultMiningSpeed(miningSpeedNum.floatValue());
            }
            tool.setDamagePerBlock(damagePerBlock);

            if (this.rulesSection != null) {
                TriggerItem.walk(this.rulesSection, new ToolComponentApplyRulesEvent(tool));
            }

            itemMeta.setTool(tool);

            if (object instanceof ItemStack itemStack) itemStack.setItemMeta(itemMeta);
            else if (object instanceof ItemType itemType) itemType.setItemMeta(itemMeta);
            else {
                Slot slot = (Slot) object;
                ItemStack slotItem = slot.getItem();
                slotItem.setItemMeta(itemMeta);
                slot.setItem(slotItem);
            }
        }

        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply tool component to " + this.items.toString(e, d);
    }

}
