package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.HeightMap;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkDataHighestY extends SimpleExpression<Number> {

    private static final boolean HAS_HEIGHT = Skript.methodExists(ChunkGenerator.ChunkData.class, "getHeight", HeightMap.class, int.class, int.class);

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprChunkDataHighestY.class, Number.class,
                "chunk[ ]data highest [block] y at %vector%")
            .name("ChunkGenerator - Highest Block Y")
            .description("Get the highest block y at a position in a chunk in a `block pop` or `surface gen` section.",
                "Getting the y in a `surface gen` section requires Paper 1.21.4+")
            .examples("set {_y} to chunkdata highest y at vector(1,0,1)")
            .since("3.5.3")
            .register();
    }

    private Expression<Vector> vector;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.vector = (Expression<Vector>) exprs[0];
        return true;
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        Vector vector = this.vector.getSingle(event);
        if (vector == null) {
            return null;
        }

        if (event instanceof BlockPopulateEvent popEvent) {
            int x = (popEvent.getChunkX() << 4) + vector.getBlockX();
            int z = (popEvent.getChunkZ() << 4) + vector.getBlockZ();
            int highest = popEvent.getLimitedRegion().getHighestBlockYAt(x, z, HeightMap.WORLD_SURFACE_WG) - 1;
            return new Number[]{highest};
        } else if (event instanceof ChunkGenEvent genEvent && HAS_HEIGHT) {
            int x = Math.clamp(vector.getBlockX(), 0, 15);
            int z = Math.clamp(vector.getBlockZ(), 0, 15);
            int highest = genEvent.getChunkData().getHeight(HeightMap.WORLD_SURFACE_WG, x, z);
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
