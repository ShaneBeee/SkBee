package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - Get")
@Description({"Get a captured state of a block, which will not change automatically.",
        "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
        "This can later be used to update, which will force the block back to this state."})
@Examples({"set {_state} to blockstate of event-block",
        "set event-block to air",
        "wait 1 minute",
        "force update {_state} without physics updates"})
@Since("2.13.0")
public class ExprBlockStateGet extends SimplePropertyExpression<Block, BlockState> {

    static {
        register(ExprBlockStateGet.class, BlockState.class,
                "(captured|block)[ ]state[s]", "blocks");
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
