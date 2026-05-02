package com.shanebeestudios.skbee.elements.worldgen.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.worldgen.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.worldgen.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.HeightMap;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkDataHighestY extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprChunkDataHighestY.class, Number.class,
                "chunk[ ]data highest [block] y at %vector% [for [heightmap] %-heightmap%]",
                "chunk[ ]data highest [block] y at %number%,[ ]%number% [for [heightmap] %-heightmap%]")
            .name("ChunkGenerator - Highest Block Y")
            .description("Get the highest block y at a position in a chunk in a `block pop` or `surface gen` section.",
                "You can optionally choose which HeightMap to use (Defaults to `ocean_floor_wg`).")
            .examples("set {_y} to chunkdata highest y at vector(1,0,1)",
                "set {_y} to chunkdata highest y at 1, 1 for heightmap world_surface_wg")
            .since("3.5.3")
            .register();
    }

    private Expression<Vector> vector;
    private Expression<Number> num1, num2;
    private Expression<HeightMap> heightMap;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern == 0) {
            this.vector = (Expression<Vector>) exprs[0];
            this.heightMap = (Expression<HeightMap>) exprs[1];
        } else {
            this.num1 = (Expression<Number>) exprs[0];
            this.num2 = (Expression<Number>) exprs[1];
            this.heightMap = (Expression<HeightMap>) exprs[2];
        }
        return true;
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        int blockX = 0;
        int blockZ = 0;

        if (this.vector != null) {
            Vector vector = this.vector.getSingle(event);
            if (vector == null) {
                return null;
            }
            blockX = vector.getBlockX();
            blockZ = vector.getBlockZ();

        } else if (this.num1 != null && this.num2 != null) {
            Number num1 = this.num1.getSingle(event);
            Number num2 = this.num2.getSingle(event);
            if (num1 == null || num2 == null) {
                return null;
            }
            blockX = num1.intValue();
            blockZ = num2.intValue();
        }


        HeightMap heightMap = HeightMap.OCEAN_FLOOR_WG;
        if (this.heightMap != null) {
            heightMap = this.heightMap.getSingle(event);
        }
        if (heightMap == null) {
            return null;
        }

        if (event instanceof BlockPopulateEvent popEvent) {
            int x = (popEvent.getChunkX() << 4) + blockX;
            int z = (popEvent.getChunkZ() << 4) + blockZ;
            int highest = popEvent.getLimitedRegion().getHighestBlockYAt(x, z, heightMap) - 1;
            return new Number[]{highest};
        } else if (event instanceof ChunkGenEvent genEvent) {
            int x = Math.clamp(blockX, 0, 15);
            int z = Math.clamp(blockZ, 0, 15);
            int highest = genEvent.getChunkData().getHeight(heightMap, x, z);
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
        String hm = this.heightMap != null ? " using heightmap " + this.heightMap.toString(e, d) : "";
        if (this.vector != null) {
            return "chunkdata highest y at " + this.vector.toString(e, d) + hm;
        } else {
            return "chunkdata highest y at " + this.num1.toString(e, d) + ", "
                + this.num2.toString(e, d) + hm;
        }
    }

}
