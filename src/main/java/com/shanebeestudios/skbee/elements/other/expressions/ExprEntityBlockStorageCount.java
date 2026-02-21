package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprEntityBlockStorageCount extends PropertyExpression<Block, Long> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprEntityBlockStorageCount.class, Long.class,
                "(size|amount) of [all] (stored entities|entities stored) in %blocks%")
            .name("EntityBlockStorage - Entity Count")
            .description("Get the amount of entities currently stored in a storage block.",
                "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+")
            .examples("if amount of stored entities in block at player > 10:",
                "set {_a} to size of entities stored in event-block")
            .since("1.0.0")
            .register();
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        setExpr((Expression<Block>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Long[] get(Event event, Block[] blocks) {
        return get(blocks, block -> {
            BlockState state = block.getState();
            if (state instanceof EntityBlockStorage) {
                return ((long) ((EntityBlockStorage<?>) state).getEntityCount());
            }
            return 0L;
        });
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "amount of entities stored in " + getExpr().toString(e, d);
    }

}
