package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkDataBiome extends SimpleExpression<Biome> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprChunkDataBiome.class, Biome.class,
                "chunk[ ]data biome",
                "chunk[ ]data biome at %vector%")
            .name("ChunkGenerator - ChunkData Biome")
            .description("Represents the biome in ChunkData.",
                "The first pattern is used to set the biome in the `biome gen` section.",
                "The second pattern is used to retrieve biomes in the `chunk gen` and `block pop` sections.",
                "NOTE: The vector represents the position of the biome in the chunk, not the world.")
            .examples("biome gen:",
                "\tset chunkdata biome to plains",
                "",
                "chunk gen:",
                "\tset {_biome} to chunkdata biome at vector(0,0,0)",
                "\tif {_biome} is plains:",
                "\t\tset chunkdata block at vector(0,0,0) to grass[]")
            .since("3.5.0")
            .register();
    }

    private Expression<Vector> vector;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern == 0 && !ParserInstance.get().isCurrentEvent(BiomeGenEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in biome gen sections.");
            return false;
        }
        if (matchedPattern == 1 && !ParserInstance.get().isCurrentEvent(ChunkGenEvent.class, BlockPopulateEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen/block pop sections.");
            return false;
        }
        this.vector = matchedPattern == 1 ? (Expression<Vector>) exprs[0] : null;
        return true;
    }

    @Override
    protected Biome @Nullable [] get(@NotNull Event event) {
        if (this.vector != null) {
            Vector vector = this.vector.getSingle(event);
            if (vector == null) {
                return null;
            }
            int x = MathUtil.clamp(vector.getBlockX(), 0, 15);
            int y = vector.getBlockY();
            int z = MathUtil.clamp(vector.getBlockZ(), 0, 15);
            if (event instanceof ChunkGenEvent chunkGenEvent) {
                ChunkData chunkData = chunkGenEvent.getChunkData();
                Biome biome = chunkData.getBiome(x, y, z);
                return new Biome[]{biome};
            } else if (event instanceof BlockPopulateEvent populateEvent) {
                int chunkX = populateEvent.getChunkX();
                int chunkZ = populateEvent.getChunkZ();
                LimitedRegion limitedRegion = populateEvent.getLimitedRegion();
                y = clamp(limitedRegion, y);
                Biome biome = limitedRegion.getBiome((chunkX << 4) + x, y, (chunkZ << 4) + z);
                return new Biome[]{biome};
            }
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET && this.vector == null) return CollectionUtils.array(Biome.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] instanceof Biome biome && event instanceof BiomeGenEvent biomeGenEvent) {
            biomeGenEvent.setBiome(biome);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Biome> getReturnType() {
        return Biome.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String vector = this.vector != null ? (" at " + this.vector.toString(e, d)) : "";
        return "chunkdata biome" + vector;
    }

    private static int clamp(LimitedRegion limitedRegion, int y) {
        World world = limitedRegion.getWorld();
        int minHeight = world.getMinHeight();
        int maxHeight = world.getMaxHeight() - 1;
        return MathUtil.clamp(y, minHeight, maxHeight);
    }

}
