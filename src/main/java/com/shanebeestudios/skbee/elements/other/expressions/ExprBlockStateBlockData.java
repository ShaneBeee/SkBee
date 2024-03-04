package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - BlockData")
@Description("Represents the blockdata of a block state.")
@Examples({"set {_data} to blockstate blockdata of {_blockstate}",
        "set blockstate blockdata of {_blockstate} to stone[]"})
@Since("2.13.0")
public class ExprBlockStateBlockData extends SimplePropertyExpression<BlockState, BlockData> {

    static {
        register(ExprBlockStateBlockData.class, BlockData.class,
                "block[ ]state block[ ]data[s]", "blockstates");
    }

    @Override
    public @Nullable BlockData convert(BlockState blockState) {
        return blockState.getBlockData();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(BlockData.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof BlockData blockData) {
            for (BlockState blockState : this.getExpr().getArray(event)) {
                blockState.setBlockData(blockData);
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "blockstate blockdate";
    }

}
