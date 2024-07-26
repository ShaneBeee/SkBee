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
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecToolComponent.ToolComponentApplyRulesEvent;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Name("ItemComponent - Tool Rule Apply")
@Description({"Apply rules to a tool component. You can add as many as you'd like.",
    "",
    "**Entries/Sections**:",
    "NOTE: One of either `block types` or `block tag` MUST be used.",
    "`block types` = The blocks to match for this rule to apply.",
    "`block tag` = A Minecraft Tag to match for this rule to apply.",
    "`speed` = If the blocks match, overrides the default mining speed. [Optional]",
    "`correct for drops` = If the blocks match, overrides whether or not this tool is " +
        "considered correct to mine at its most efficient speed, and to drop items if the block's loot table requires it. [Optional]"})
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
public class SecToolRule extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATIOR = EntryValidator.builder();

    static {
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("block types", null, true, ItemType.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("block tag", null, true, Tag.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("speed", null, true, Number.class));
        VALIDATIOR.addEntryData(new ExpressionEntryData<>("correct for drops", null, true, Boolean.class));
        Skript.registerSection(SecToolRule.class, "apply tool rule");
    }

    private Expression<ItemType> blockTypes;
    private Expression<Tag<Material>> blockKey;
    private Expression<Number> speed;
    private Expression<Boolean> correctForDrops;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(ToolComponentApplyRulesEvent.class)) {
            Skript.error("Tool rules can only be applied in a 'rules' section of a tool component section.");
            return false;
        }

        EntryContainer container = VALIDATIOR.build().validate(sectionNode);
        if (container == null) return false;

        this.blockTypes = (Expression<ItemType>) container.getOptional("block types", false);
        this.blockKey = (Expression<Tag<Material>>) container.getOptional("block tag", false);
        if (this.blockTypes == null && this.blockKey == null) {
            Skript.error("Either a 'block types' or 'block tag' entry needs to be used.");
            return false;
        }
        this.speed = (Expression<Number>) container.getOptional("speed", false);
        this.correctForDrops = (Expression<Boolean>) container.getOptional("correct for drops", false);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (event instanceof ToolComponentApplyRulesEvent rulesEvent) {
            ToolComponent component = rulesEvent.getComponent();

            Number speedNum = this.speed != null ? this.speed.getSingle(event) : null;
            Float speed = speedNum != null ? speedNum.floatValue() : null;
            Boolean correctForDrops = this.correctForDrops != null ? this.correctForDrops.getSingle(event) : null;

            if (this.blockTypes != null) {
                List<Material> blockMaterials = new ArrayList<>();
                for (ItemType itemType : this.blockTypes.getArray(event)) {
                    Material material = itemType.getMaterial();
                    if (material.isBlock()) blockMaterials.add(material);
                }
                component.addRule(blockMaterials, speed, correctForDrops);
            } else if (this.blockKey != null) {
                Tag<Material> tag = this.blockKey.getSingle(event);
                if (tag != null) {
                    component.addRule(tag, speed, correctForDrops);
                }
            }
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply tool rule";
    }
}
