package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("EntityBlockStorage - Entity Count")
@Description({"Get the amount of entities currently stored in a storage block.",
        "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+"})
@Examples({"if amount of stored entities in block at player > 10:",
        "set {_a} to size of entities stored in event-block"})
@Since("1.0.0")
public class ExprEntityBlockStorageCount extends PropertyExpression<Block, Long> {

    static {
        Skript.registerExpression(ExprEntityBlockStorageCount.class, Long.class, ExpressionType.PROPERTY,
                "(size|amount) of [all] (stored entities|entities stored) in %blocks%");
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
