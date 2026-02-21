package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprBlockDataUpdates extends SimpleExpression<BlockData> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBlockDataUpdates.class, BlockData.class,
                "block[ ](data|state) of %blocks% without update[s]")
            .name("BlockData - Updates")
            .description("Set the BlockData of a block without updates (will prevent physics updates of neighbouring blocks).")
            .examples("set blockdata of target block without updates to oak_fence[]",
                "set blockdata of target block without updates to campfire[lit=false]")
            .since("2.6.0")
            .register();
    }

    private Expression<Block> blocks;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, @NotNull Kleenean kleenean, ParseResult parseResult) {
        this.blocks = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected BlockData @NotNull [] get(@NotNull Event event) {
        List<BlockData> list = new ArrayList<>();
        for (Block block : this.blocks.getAll(event)) {
            list.add(block.getBlockData());
        }
        return list.toArray(new BlockData[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BlockData.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(@NotNull Event e, Object[] delta, @NotNull ChangeMode mode) {
        for (Block block : blocks.getAll(e)) {
            BlockData blockData;
            try {
                if (delta != null) {
                    Object object = delta[0];
                    if (object instanceof BlockData bd) {
                        blockData = bd;
                    } else if (object instanceof String string) {
                        blockData = Bukkit.createBlockData(string);
                    } else if (object instanceof ItemType itemType) {
                        blockData = itemType.getMaterial().createBlockData();
                    } else {
                        return;
                    }
                    block.setBlockData(blockData, false);
                }
            } catch (IllegalArgumentException ex) {
                Util.debug("Could not parse block data: %s", delta[0]);
            }
        }

    }

    @Override
    public @NotNull Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "block data of " + this.blocks.toString(e, d) + " without updates";
    }

}
