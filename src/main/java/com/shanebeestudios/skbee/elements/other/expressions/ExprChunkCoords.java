package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkCoords extends PropertyExpression<Chunk, Number> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprChunkCoords.class, Number.class,
                "chunk (:x|z) of %chunk%")
            .name("Chunk Coordinates")
            .description("Represents the coordinates of a chunk.")
            .examples("set {_x} to chunk x of chunk at player")
            .since("1.13.0")
            .register();
    }

    private boolean x;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.x = parseResult.hasTag("x");
        setExpr((Expression<? extends Chunk>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @NotNull [] get(Event event, Chunk[] source) {
        return get(source, chunk -> this.x ? chunk.getX() : chunk.getZ());
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String x = this.x ? "x" : "z";
        return "chunk " + x + " coord of " + getExpr().toString(e, d);
    }

}
