package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.wrapper.BlockStateWrapper;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - Block")
@Description("Gets the block represented by this block state.")
@Examples("set {_block} to block of {_blockstate}")
@Since("2.13.0")
public class ExprBlockStateBlock extends SimplePropertyExpression<BlockStateWrapper, Block> {

    static {
        register(ExprBlockStateBlock.class, Block.class, "block[s]", "blockstates");
    }

    @Override
    public @Nullable Block convert(BlockStateWrapper blockStateWrapper) {
        return blockStateWrapper.getBukkitBlockState().getBlock();
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
