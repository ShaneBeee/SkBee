package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBlockStateGet extends SimplePropertyExpression<Block, BlockState> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBlockStateGet.class, BlockState.class,
                "(captured|block)[ ]state[s]", "blocks")
            .name("BlockState - Get")
            .description("Get a captured state of a block, which will not change automatically.",
                "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
                "This can later be used to update, which will force the block back to this state.")
            .examples("set {_state} to blockstate of event-block",
                "set event-block to air",
                "wait 1 minute",
                "force update {_state} without physics updates")
            .since("2.13.0")
            .register();
    }

    @Override
    public @Nullable BlockState convert(Block block) {
        return block.getState();
    }

    @Override
    public @NotNull Class<? extends BlockState> getReturnType() {
        return BlockState.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "block state";
    }

}
