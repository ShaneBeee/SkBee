package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.BlockStateWrapper;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - BlockData")
@Description("Represents the blockdata of a block state.")
@Examples("set {_data} to blockstate blockdata of {_blockstate}")
@Since("INSERT VERSION")
public class ExprBlockStateBlockData extends SimpleExpression<BlockData> {

    static {
        Skript.registerExpression(ExprBlockStateBlockData.class, BlockData.class, ExpressionType.PROPERTY,
                "block[ ]state block[ ]data[s] of %blockstates%");
    }

    private Expression<BlockStateWrapper> blockState;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockState = (Expression<BlockStateWrapper>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected BlockData[] get(Event event) {
        List<BlockData> blockDatas = new ArrayList<>();
        for (BlockStateWrapper blockState : blockState.getAll(event)) {
            blockDatas.add(blockState.getBlockData());
        }
        return blockDatas.toArray(new BlockData[0]);
    }

    @Override
    public boolean isSingle() {
        return blockState.isSingle();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "blockstate blockdata of " + blockState.toString(e, d);
    }

}
