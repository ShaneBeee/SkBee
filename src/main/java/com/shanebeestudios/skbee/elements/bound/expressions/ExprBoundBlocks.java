package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - Blocks")
@Description("All the blocks within a bound")
@Examples({"set {_blocks::*} to all blocks within bound {bound}",
        "set all blocks within {bound} to stone",
        "loop all blocks within bound {bound}:", "\tif loop-block is stone:", "\t\tset loop-block to grass"})
@Since("1.0.0")
public class ExprBoundBlocks extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprBoundBlocks.class, Block.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] blocks within bound %bound%");
    }

    private Expression<Bound> bound;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        bound = (Expression<Bound>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Block[] get(Event event) {
        Bound single = bound.getSingle(event);
        if (single == null) {
            return null;
        }
        List<Block> list = new ArrayList<>(single.getBlocks());
        return list.toArray(new Block[0]);
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "the blocks within bound " + bound.toString(e, d);
    }

}
