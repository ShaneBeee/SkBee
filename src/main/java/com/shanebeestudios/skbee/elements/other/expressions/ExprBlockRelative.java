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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Block - Relative")
@Description("Get a block relative to another block using a BlockFace.")
@Examples({"set {_rel} to block relative to target block using {_blockFace}",
        "set block relative to target block using north to stone"})
@Since("2.6.0")
public class ExprBlockRelative extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprBlockRelative.class, Block.class, ExpressionType.COMBINED,
                "block[s] relative to %block% (from|using) %blockfaces%");
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
