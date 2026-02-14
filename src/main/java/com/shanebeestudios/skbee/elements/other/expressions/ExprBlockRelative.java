package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprBlockRelative extends SimpleExpression<Block> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBlockRelative.class, Block.class,
                "block[s] relative to %block% (from|using) %blockfaces%")
            .name("Block - Relative")
            .description("Get a block relative to another block using a BlockFace.")
            .examples("set {_rel} to block relative to target block using {_blockFace}",
                "set block relative to target block using north to stone")
            .since("2.6.0")
            .register();
    }

    private Expression<Block> block;
    private Expression<BlockFace> blockFaces;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.block = (Expression<Block>) exprs[0];
        this.blockFaces = (Expression<BlockFace>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Block[] get(Event event) {
        Block block = this.block.getSingle(event);
        List<Block> blocks = new ArrayList<>();

        if (block == null) return null;

        for (BlockFace blockFace : this.blockFaces.getArray(event)) {
            blocks.add(block.getRelative(blockFace));
        }

        return blocks.toArray(new Block[0]);
    }

    @Override
    public boolean isSingle() {
        return this.blockFaces.isSingle();
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "block[s] relative to " + this.block.toString(e,d) + " using " + this.blockFaces.toString(e,d);
    }

}
