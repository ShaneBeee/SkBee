package com.shanebeestudios.skbee.elements.generator.expressions;

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
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import org.bukkit.HeightMap;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ChunkGenerator - Highest Block Y")
@Description("Get the highest block y at a position in a chunk in a `block pop` section.")
@Examples("set {_y} to chunkdata highest y at vector(1,0,1)")
@Since("INSERT VERSION")
public class ExprChunkDataHighestY extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprChunkDataHighestY.class, Number.class, ExpressionType.SIMPLE,
            "chunk[ ]data highest [block] y at %vector%");
    }

    private Expression<Vector> vector;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.vector = (Expression<Vector>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        Vector vector = this.vector.getSingle(event);
        if (vector == null) return null;
        if (event instanceof BlockPopulateEvent popEvent) {
            int x = (popEvent.getChunkX() << 4) + vector.getBlockX();
            int z = (popEvent.getChunkZ() << 4) + vector.getBlockZ();
            int highest = popEvent.getLimitedRegion().getHighestBlockYAt(x, z, HeightMap.WORLD_SURFACE_WG);
            return new Number[]{highest};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "chunkdata highest y at " + vector.toString(e, d);
    }

}
