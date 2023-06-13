package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.BlockStateWrapper;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - BlockData")
@Description("Represents the blockdata of a block state.")
@Examples({"set {_data} to blockstate blockdata of {_blockstate}",
        "set blockstate blockdata of {_blockstate} to stone[]"})
@Since("INSERT VERSION")
public class ExprBlockStateBlockData extends SimplePropertyExpression<BlockStateWrapper, BlockData> {

    static {
        register(ExprBlockStateBlockData.class, BlockData.class,
                "block[ ]state block[ ]data[s]", "blockstates");
    }

    @Override
    public @Nullable BlockData convert(BlockStateWrapper blockStateWrapper) {
        return blockStateWrapper.getBlockData();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(BlockData.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof BlockData blockData) {
            for (BlockStateWrapper blockStateWrapper : this.getExpr().getArray(event)) {
                blockStateWrapper.setBlockData(blockData);
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
