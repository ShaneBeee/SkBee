package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
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
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Tool Component Apply")
@Description({"Apply a tool component to any item making it usable tool.",
    "Requires Paper 1.21.3+",
    "See [**Tool Component**](https://minecraft.wiki/w/Data_component_format#tool) on McWiki for more details.",
    "",
    "**Entries/Sections**:",
    "- `default_mining_speed` = The default mining speed of this tool, used if no rules override it. Defaults to 1.0. [Optional]",
    "- `damage_per_block` = The amount of durability to remove each time a block is broken with this tool. Must be a non-negative integer. [Optional]",
    "- `rules:` =  A list of rules for the blocks that this tool has a special behavior with."})
@Examples({"set {_i} to a stick",
    "apply tool component to {_i}:",
    "\tdefault_mining_speed: 2.3",
    "\tdamage_per_block: 2",
    "\trules:",
    "\t\tapply tool rule:",
    "\t\t\tblock_tag: minecraft block tag \"minecraft:dirt\"",
    "\t\t\tspeed: 1.0",
    "\t\t\tcorrect_for_drops: true",
    "\t\tapply tool rule:",
    "\t\t\tblock_types: granite, stone and andesite",
    "\t\t\tspeed: 0.5",
    "\t\t\tcorrect_for_drops: false",
    "give {_i} to player"})
@Since("INSERT VERSION")
public class SecToolComponent extends Section {

    public static class ToolComponentApplyRulesEvent extends Event {

        private final Tool.Builder toolBuilder;

        public ToolComponentApplyRulesEvent(Tool.Builder toolBuilder) {
            this.toolBuilder = toolBuilder;
        }

        public Tool.Builder getToolBuilder() {
            return this.toolBuilder;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException("ToolComponentApplyRulesEvent should never be called");
        }
    }

    private static final EntryValidator VALIDATOR;

    static {
        EntryValidator.EntryValidatorBuilder builder = EntryValidator.builder();
        builder.addEntryData(new ExpressionEntryData<>("default_mining_speed", null, true, Number.class));
        builder.addEntryData(new ExpressionEntryData<>("damage_per_block", null, true, Number.class));
        builder.addSection("rules", true);
        Skript.registerSection(SecToolComponent.class, "apply tool component to %itemstacks/itemtypes/slots%");
        VALIDATOR = builder.build();
    }

    private Expression<ItemType> items;
    private Expression<Number> defaultMiningSpeed;
    private Expression<Number> damagePerBlock;
    private Trigger rulesSection;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.items = (Expression<ItemType>) exprs[0];
        this.defaultMiningSpeed = (Expression<Number>) container.getOptional("default_mining_speed", false);
        this.damagePerBlock = (Expression<Number>) container.getOptional("damage_per_block", false);

        SectionNode rulesNode = container.getOptional("rules", SectionNode.class, false);
        if (rulesNode != null) {
            this.rulesSection = loadCode(rulesNode, "rules section", ToolComponentApplyRulesEvent.class);
        }
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        Tool.Builder toolBuilder = Tool.tool();
        if (this.defaultMiningSpeed != null) {
            float speed = this.defaultMiningSpeed.getOptionalSingle(event).orElse(1.0).floatValue();
            toolBuilder.defaultMiningSpeed(speed);
        }
        if (this.damagePerBlock != null) {
            int damage = this.damagePerBlock.getOptionalSingle(event).orElse(1).intValue();
            toolBuilder.damagePerBlock(damage);
        }

        if (this.rulesSection != null) {
            ToolComponentApplyRulesEvent toolEvent = new ToolComponentApplyRulesEvent(toolBuilder);
            Variables.setLocalVariables(toolEvent, localVars);
            TriggerItem.walk(this.rulesSection, toolEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(toolEvent));
            Variables.removeLocals(toolEvent);
        }

        Tool tool = toolBuilder.build();

        ItemUtils.modifyComponent(this.items.getArray(event), ChangeMode.SET, DataComponentTypes.TOOL, tool);

        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply tool component to " + this.items.toString(e, d);
    }

}
