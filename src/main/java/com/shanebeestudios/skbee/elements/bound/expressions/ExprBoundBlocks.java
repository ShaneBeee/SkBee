package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBoundBlocks extends SimpleExpression<Block> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBoundBlocks.class, Block.class,
                "[(all [[of] the]|the)] bound blocks within %bound%",
                "[(all [[of] the]|the)] blocks within bound %bound%")
            .name("Bound - Blocks")
            .description("Get all of the blocks within a bound.")
            .examples("set {_blocks::*} to all blocks within bound {bound}",
                "set {_blocks::*} to bound blocks within bound with id \"le-bound\"",
                "set all bound blocks within {bound} to stone",
                "loop all bound blocks within {bound}:",
                "\tif loop-block is stone:",
                "\t\tset loop-block to grass")
            .since("1.0.0")
            .register();
    }

    private Expression<Bound> bound;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.bound = (Expression<Bound>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Block @Nullable [] get(Event event) {
        Bound bound = this.bound.getSingle(event);
        if (bound == null) {
            return null;
        }
        return bound.getBlocks().toArray(new Block[0]);
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
        return "bound blocks within " + this.bound.toString(e, d);
    }

}
