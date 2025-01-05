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
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecToolComponent.ToolComponentApplyRulesEvent;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlockType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "UnstableApiUsage"})
@Name("ItemComponent - Tool Rule Apply")
@Description({"Apply rules to a tool component. You can add as many as you'd like.",
    "See [**McWiki Tool Component**](https://minecraft.wiki/w/Data_component_format#tool) for more details.",
    "",
    "**Entries/Sections**:",
    "NOTE: One of either `block_types` or `block_tag` MUST be used.",
    "`block_types` = The blocks (ItemTypes) to match for this rule to apply.",
    "`block_tag` = A Minecraft Block Tag to match for this rule to apply.",
    "`speed` = If the blocks match, overrides the default mining speed (Must be a positive number). [Optional]",
    "`correct for drops` = If the blocks match, overrides whether or not this tool is " +
        "considered correct to mine at its most efficient speed, and to drop items if the block's loot table requires it. [Optional]"})
@Examples({"set {_i} to a stick",
    "apply tool component to {_i}:",
    "\tdefault_mining_speed: 2.3",
    "\tdamage_per_block: 2",
    "\trules:",
    "\t\tapply tool rule:",
    "\t\t\tblock_tag: minecraft block tag \"minecraft:all_signs\"",
    "\t\t\tspeed: 1.0",
    "\t\t\tcorrect_for_drops: true",
    "\t\tapply tool rule:",
    "\t\t\tblock_types: granite, stone and andesite",
    "\t\t\tspeed: 0.5",
    "\t\t\tcorrect_for_drops: false",
    "give {_i} to player"})
@Since("INSERT VERSION")
public class SecToolRule extends Section {

    private static final EntryValidator VALIDATOR;
    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess.registryAccess();

    static {
        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("block_types", ItemType.class)
            .addOptionalEntry("block_tag", Tag.class)
            .addOptionalEntry("speed", Number.class)
            .addOptionalEntry("correct_for_drops", Boolean.class)
            .build();
        Skript.registerSection(SecToolRule.class, "apply tool rule");
    }

    private Expression<ItemType> blockTypes;
    private Expression<Tag<Material>> blockTag;
    private Expression<Number> speed;
    private Expression<Boolean> correctForDrops;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(ToolComponentApplyRulesEvent.class)) {
            Skript.error("Tool rules can only be applied in a 'rules' section of a tool component section.");
            return false;
        }

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) return false;

        this.blockTypes = (Expression<ItemType>) container.getOptional("block_types", false);
        this.blockTag = (Expression<Tag<Material>>) container.getOptional("block_tag", false);
        if (this.blockTypes == null && this.blockTag == null) {
            Skript.error("Either a 'block_types' or 'block_tag' entry needs to be used.");
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
            Tool.Builder toolBuilder = rulesEvent.getToolBuilder();

            RegistryKeySet<BlockType> registryKeySet = null;
            if (this.blockTypes != null) {
                List<TypedKey<BlockType>> typedKeys = new ArrayList<>();
                for (ItemType itemType : this.blockTypes.getArray(event)) {
                    TypedKey<BlockType> typedKey = TypedKey.create(RegistryKey.BLOCK, itemType.getMaterial().key());
                    typedKeys.add(typedKey);
                }
                registryKeySet = RegistrySet.keySet(RegistryKey.BLOCK, typedKeys);
            } else if (this.blockTag != null) {
                Tag<Material> bukkitTag = this.blockTag.getSingle(event);
                TagKey<BlockType> tagKey = TagKey.create(RegistryKey.BLOCK, bukkitTag.key());
                registryKeySet = REGISTRY_ACCESS.getRegistry(RegistryKey.BLOCK).getTag(tagKey);
            }
            Float speed = null;
            if (this.speed != null) {
                Number num = this.speed.getSingle(event);
                if (num != null) speed = num.floatValue();
            }
            TriState correctForDrops = this.correctForDrops == null ? TriState.NOT_SET : TriState.byBoolean(this.correctForDrops.getSingle(event));

            Tool.Rule rule = Tool.rule(registryKeySet, speed, correctForDrops);
            toolBuilder.addRule(rule);
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply tool rule";
    }

}
