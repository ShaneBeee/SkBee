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
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registry.RegistryUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SimpleEntryValidator;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@Name("ItemComponent - Adventure Predicate Apply")
@Description({"Apply an adventure can break/can place on predicate to items.",
    "Requires Paper 1.21.3+",
    "See [**Can Break**](https://minecraft.wiki/w/Data_component_format#can_break)/" +
        "[**Can Place On**](https://minecraft.wiki/w/Data_component_format#can_place_on) components on McWiki for more info.",
    "",
    "**Entries**:",
    "- `blocks` = The blocks this item can place on/can break. (Accepts ItemTypes, BlockDatas, Tags, TagKeys and RegistryKeySets(soon™))",
    "- `show_in_tooltip` = Show or hide the \"Can break/Can Place On\" line on this item's tooltip. (Optional, defaults to true)"})
@Examples({"apply can break adventure predicate to player's tool:",
    "\tblocks: grass block, dirt, stone, gravel",
    "",
    "apply can break adventure predicate to player's tool:",
    "\tblocks: minecraft block tag \"minecraft:mineable/pickaxe\"",
    "",
    "apply can place on adventure predicate to player's tool:",
    "\tblocks: minecraft block tag \"minecraft:logs\""})
@Since("INSERT VERSION")
@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class SecAdventureComponent extends Section {

    private static final EntryValidator VALIDATOR;

    static {
        @SuppressWarnings("unchecked")
        Class<Object>[] classes = (Class<Object>[]) CollectionUtils.array(ItemType.class, BlockData.class,
            Tag.class, RegistryKeySet.class, TagKey.class);

        VALIDATOR = SimpleEntryValidator.builder()
            .addRequiredEntry("blocks", classes)
            .addOptionalEntry("show_in_tooltip", Boolean.class)
            .build();
        Skript.registerSection(SecAdventureComponent.class,
            "apply (place:can place on|can break) [adventure] predicate to %itemstacks/itemtypes/slots%");
    }

    private Expression<?> items;
    private Expression<?> blocks;
    private Expression<Boolean> showInTooltip;
    private DataComponentType.Valued<ItemAdventurePredicate> dataType;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer validate = VALIDATOR.validate(sectionNode);
        if (validate == null) {
            return false;
        }
        this.items = exprs[0];
        this.blocks = validate.getOptional("blocks", Object.class, false);
        this.showInTooltip = (Expression<Boolean>) validate.getOptional("show_in_tooltip", false);
        this.dataType = parseResult.hasTag("place") ? DataComponentTypes.CAN_PLACE_ON : DataComponentTypes.CAN_BREAK;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        boolean showInTooltip = this.showInTooltip != null && this.showInTooltip.getOptionalSingle(event).orElse(true);

        List<TypedKey<BlockType>> blockTypes = new ArrayList<>();
        ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();

        for (Object object : this.blocks.getArray(event)) {
            if (object instanceof ItemType itemType) {
                TypedKey<BlockType> blockType = TypedKey.create(RegistryKey.BLOCK, itemType.getMaterial().key());
                if (!blockTypes.contains(blockType)) {
                    blockTypes.add(blockType);
                }
            } else if (object instanceof BlockData blockData) {
                TypedKey<BlockType> blockType = TypedKey.create(RegistryKey.BLOCK, blockData.getMaterial().key());
                if (!blockTypes.contains(blockType)) {
                    blockTypes.add(blockType);
                }
            } else if (object instanceof Tag<?> tag) {
                RegistryKeySet<BlockType> keySet = RegistryUtils.getKeySet(tag, RegistryKey.BLOCK);
                builder.addPredicate(BlockPredicate.predicate().blocks(keySet).build());
            } else if (object instanceof RegistryKeySet<?> keySet && keySet.registryKey() == RegistryKey.BLOCK) {
                builder.addPredicate(BlockPredicate.predicate().blocks((RegistryKeySet<BlockType>) keySet).build());
            } else if (object instanceof TagKey<?> tagKey && tagKey.registryKey() == RegistryKey.BLOCK) {
                Registry<BlockType> registry = RegistryUtils.getRegistry(RegistryKey.BLOCK);
                if (registry.hasTag((TagKey<BlockType>) tagKey)) {
                    builder.addPredicate(BlockPredicate.predicate().blocks(registry.getTag((TagKey<BlockType>) tagKey)).build());

                }
            }
        }
        if (!blockTypes.isEmpty()) {
            RegistryKeySet<BlockType> keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockTypes);
            builder.addPredicate(BlockPredicate.predicate().blocks(keySet).build());
        }

        ItemAdventurePredicate predicate = builder.showInTooltip(showInTooltip).build();

        ItemUtils.modifyItems(this.items.getArray(event), itemStack ->
            itemStack.setData(this.dataType, predicate));
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("apply");
        builder.append(this.dataType == DataComponentTypes.CAN_PLACE_ON ? "can place on" : "can break");
        builder.append("adventure predicate to", this.items);
        return builder.toString();
    }

}
