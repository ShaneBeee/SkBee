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
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Chunk Coordinates")
@Description("Represents the coordinates of a chunk.")
@Examples("set {_x} to chunk x of chunk at player")
@Since("1.13.0")
public class ExprChunkCoords extends PropertyExpression<Chunk, Number> {

    static {
        Skript.registerExpression(ExprChunkCoords.class, Number.class, ExpressionType.PROPERTY,
                "chunk (x|1¦z) of %chunk%");
    }

    private int parseMark;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        parseMark = parseResult.mark;
        setExpr((Expression<? extends Chunk>) exprs[0]);
        return true;
    }

    @Override
    protected Number @NotNull [] get(Event event, Chunk[] source) {
        return get(source, chunk -> parseMark == 0 ? chunk.getX() : chunk.getZ());
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "chunk X/Z coord of " + getExpr().toString(e, d);
    }

}
