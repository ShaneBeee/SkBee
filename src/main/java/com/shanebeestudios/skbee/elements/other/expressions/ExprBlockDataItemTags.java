package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.BlockDataUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExprBlockDataItemTags extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBlockDataItemTags.class, String.class,
                "item [block[ ]](data|state) tags", "itemtypes")
            .name("BlockData - Item BlockData Tags")
            .description("Get all the tags from the BlockData of an item.")
            .examples("set {_tags::} to item blockdata tags of player's tool")
            .since("3.4.0")
            .register();
    }

    private Expression<ItemType> itemTypes;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemTypes = (Expression<ItemType>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        List<String> tags = new ArrayList<>();
        for (ItemType itemType : this.itemTypes.getArray(event)) {
            if (!(itemType.getItemMeta() instanceof BlockDataMeta blockDataMeta)) continue;

            Material blockForm = BlockDataUtils.getBlockForm(itemType.getMaterial());
            if (blockForm == null || !blockForm.isBlock()) continue;

            BlockData blockData = blockDataMeta.getBlockData(blockForm);
            String[] data = BlockDataUtils.getBlockDataTags(blockData);
            if (data != null) {
                tags.addAll(Arrays.asList(data));
            }
        }
        return tags.toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<String> getReturnType() {
        return String.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item blockdata tags of " + this.itemTypes.toString(e, d);
    }

}
