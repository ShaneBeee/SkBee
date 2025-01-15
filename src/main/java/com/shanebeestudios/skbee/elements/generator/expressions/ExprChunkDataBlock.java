package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ChunkGenerator - ChunkData Block")
@Description({"Represents blocks in a ChunkData.",
    "The first pattern is used to set a block in a `chunk gen` or `block pop` section.",
    "The second pattern is used to fill blocks between 2 points in a `chunk gen` section.",
    "NOTE: The vector represents the position in a chunk, not a world.",
    "NOTE: You CAN reach into neighbouring chunks going below 0/above 15 in the vector. I don't know how far you can safely reach though."})
@Examples({"chunk gen:",
    "\tset chunkdata blocks within vector({_x}, 0, {_z}) and vector({_x}, {_y}, {_z}) to red_concrete[]",
    "\tset chunkdata block at vector({_x}, {_y}, {_z}) to red_concrete_powder[]"})
@Since("3.5.0")
public class ExprChunkDataBlock extends SimpleExpression<BlockData> {

    static {
        Skript.registerExpression(ExprChunkDataBlock.class, BlockData.class, ExpressionType.COMBINED,
            "chunk[ ]data block[data] at %vector%",
            "chunk[ ]data block[data]s (between|within) %vector% (and|to) %vector%");
    }

    private Expression<Vector> vector;
    private Expression<Vector> vector2;

    @SuppressWarnings("unchecked")
    @Override

    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern == 1 && !ParserInstance.get().isCurrentEvent(ChunkGenEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen sections.");
            return false;
        } else if (matchedPattern == 0 && !ParserInstance.get().isCurrentEvent(ChunkGenEvent.class, BlockPopulateEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen/block pop sections.");
            return false;
        }
        this.vector = (Expression<Vector>) exprs[0];
        if (matchedPattern == 1) this.vector2 = (Expression<Vector>) exprs[1];

        return true;
    }

    @Override
    protected BlockData @Nullable [] get(Event event) {
        Vector vec = this.vector.getSingle(event);
        if (vec == null) {
            error("Vector is invalid: " + this.vector.toString(event, true));
            return null;
        }

        if (event instanceof ChunkGenEvent chunkGenEvent) {
            int x = vec.getBlockX();
            int y = vec.getBlockY();
            int z = vec.getBlockZ();
            return new BlockData[]{chunkGenEvent.getChunkData().getBlockData(x, y, z)};
        } else if (event instanceof BlockPopulateEvent popEvent) {
            int x = (popEvent.getChunkX() << 4) + vec.getBlockX();
            int y = vec.getBlockY();
            int z = (popEvent.getChunkZ() << 4) + vec.getBlockZ();
            return new BlockData[]{popEvent.getLimitedRegion().getBlockData(x, y, z)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(BlockData.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] instanceof BlockData blockData) {
            Vector vec = this.vector.getSingle(event);
            Vector vec2 = this.vector2 != null ? this.vector2.getSingle(event) : null;
            if (vec == null) {
                error("Invalid vector: " + this.vector.toString(event, true));
                return;
            }
            if (event instanceof ChunkGenEvent chunkGenEvent) {
                int x = vec.getBlockX();
                int y = vec.getBlockY();
                int z = vec.getBlockZ();
                if (vec2 != null) {
                    int x2 = vec2.getBlockX();
                    int y2 = vec2.getBlockY();
                    int z2 = vec2.getBlockZ();
                    setRegion(chunkGenEvent.getChunkData(), x, y, z, x2, y2, z2, blockData);
                } else {
                    chunkGenEvent.getChunkData().setBlock(x, y, z, blockData);
                }
            } else if (event instanceof BlockPopulateEvent popEvent) {
                int x = (popEvent.getChunkX() << 4) + vec.getBlockX();
                int y = vec.getBlockY();
                int z = (popEvent.getChunkZ() << 4) + vec.getBlockZ();
                LimitedRegion region = popEvent.getLimitedRegion();
                if (region.isInRegion(x, y, z)) {
                    // Make sure we are setting within the available region
                    region.setBlockData(x, y, z, blockData);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String vec = this.vector.toString(e, d);
        if (this.vector2 != null) {
            return "chunkdata blockdata within " + vec + " and " + this.vector2.toString(e, d);
        }
        return "chunkdata blockdata at " + vec;
    }

    private static void setRegion(ChunkGenerator.ChunkData chunkData, int x, int y, int z, int x2, int y2, int z2, BlockData blockData) {
        int minX = Math.min(x, x2);
        int minY = Math.min(y, y2);
        int minZ = Math.min(z, z2);
        int maxX = Math.max(x, x2) + 1;
        int maxY = Math.max(y, y2) + 1;
        int maxZ = Math.max(z, z2) + 1;
        chunkData.setRegion(minX, minY, minZ, maxX, maxY, maxZ, blockData);
    }

}
