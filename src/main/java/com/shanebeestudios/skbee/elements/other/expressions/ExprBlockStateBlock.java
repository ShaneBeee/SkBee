package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBlockStateBlock extends SimplePropertyExpression<BlockState, Block> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBlockStateBlock.class, Block.class,
                "block[s]", "blockstates")
            .name("BlockState - Block")
            .description("Gets the block represented by this block state. Will return nothing if blockstate is not placed in the world.")
            .examples("set {_block} to block of {_blockstate}")
            .since("2.13.0")
            .register();
    }

    @Override
    public @Nullable Block convert(BlockState blockState) {
        if (blockState.isPlaced()) {
            return blockState.getBlock();
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "block";
    }

}
