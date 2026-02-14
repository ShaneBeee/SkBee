package com.shanebeestudios.skbee.elements.itemcomponent.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecToolComponent.ToolComponentApplyRulesEvent;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.util.TriState;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "UnstableApiUsage"})
public class SecToolRule extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {
        @SuppressWarnings("unchecked")
        Class<Object>[] classes = (Class<Object>[]) CollectionUtils.array(ItemType.class, BlockData.class,
            Tag.class, RegistryKeySet.class, TagKey.class);

        VALIDATOR = SimpleEntryValidator.builder()
            .addOptionalEntry("blocks", classes)
            .addOptionalEntry("speed", Number.class)
            .addOptionalEntry("correct_for_drops", Boolean.class)
            .build();

        reg.newSection(SecToolRule.class, "apply tool rule")
            .name("ItemComponent - Tool Rule Apply")
            .description("Apply rules to a tool component. You can add as many as you'd like.",
                "See [**McWiki Tool Component**](https://minecraft.wiki/w/Data_component_format#tool) for more details.",
                "",
                "**Entries/Sections**:",
                "NOTE: One of either `block_types` or `block_tag` MUST be used.",
                "`blocks` = The blocks to match for this rule to apply (Supports ItemTypes, BlockDatas, Minecraft Tags and TagKeys).",
                "`speed` = If the blocks match, overrides the default mining speed (Must be a positive number). [Optional]",
                "`correct for drops` = If the blocks match, overrides whether or not this tool is " +
                    "considered correct to mine at its most efficient speed, and to drop items if the block's loot table requires it. [Optional]")
            .examples("set {_i} to a stick",
                "apply tool component to {_i}:",
                "\tdefault_mining_speed: 2.3",
                "\tdamage_per_block: 2",
                "\trules:",
                "\t\tapply tool rule:",
                "\t\t\tblocks: minecraft block tag \"minecraft:all_signs\" # Shown as a Minecraft block tag",
                "\t\t\tspeed: 1.0",
                "\t\t\tcorrect_for_drops: true",
                "\t\tapply tool rule:",
                "\t\t\tblocks: stone, granite, andesite and gravel # Shown as a list of ItemTypes",
                "\t\t\tspeed: 0.5",
                "\t\t\tcorrect_for_drops: false",
                "give {_i} to player")
            .since("3.8.0")
            .register();
    }

    private Expression<?> blocks;
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

        this.blocks = (Expression<ItemType>) container.getOptional("blocks", false);
        this.speed = (Expression<Number>) container.getOptional("speed", false);
        this.correctForDrops = (Expression<Boolean>) container.getOptional("correct_for_drops", false);
        return true;
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (event instanceof ToolComponentApplyRulesEvent rulesEvent) {
            Tool.Builder toolBuilder = rulesEvent.getToolBuilder();

            RegistryKeySet<BlockType> registryKeySet = null;
            if (this.blocks != null) {
                List<TypedKey<BlockType>> typedKeys = new ArrayList<>();

                for (Object object : this.blocks.getArray(event)) {
                    if (object instanceof ItemType itemType) {
                        TypedKey<BlockType> typedKey = TypedKey.create(RegistryKey.BLOCK, itemType.getMaterial().key());
                        typedKeys.add(typedKey);
                    } else if (object instanceof BlockData blockData) {
                        TypedKey<BlockType> typedKey = TypedKey.create(RegistryKey.BLOCK, blockData.getMaterial().key());
                        typedKeys.add(typedKey);
                    } else if (object instanceof Tag<?> tag) {
                        // Clear the keys in the event we have a tag
                        // We can't have both (either a list of blocks OR 1 tag)
                        registryKeySet = RegistryUtils.getKeySet(tag, RegistryKey.BLOCK);
                        typedKeys.clear();
                        break;
                    } else if (object instanceof RegistryKeySet<?> keySet && keySet.registryKey() == RegistryKey.BLOCK) {
                        registryKeySet = (RegistryKeySet<BlockType>) keySet;
                        typedKeys.clear();
                        break;
                    } else if (object instanceof TagKey<?> tagKey && tagKey.registryKey() == RegistryKey.BLOCK) {
                        Registry<BlockType> registry = RegistryUtils.getRegistry(RegistryKey.BLOCK);
                        if (registry.hasTag((TagKey<BlockType>) tagKey)) {
                            registryKeySet = registry.getTag((TagKey<BlockType>) tagKey);
                            typedKeys.clear();
                            break;
                        }
                    }
                }

                if (!typedKeys.isEmpty()) {
                    registryKeySet = RegistrySet.keySet(RegistryKey.BLOCK, typedKeys);
                }
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
