package com.shanebeestudios.skbee.elements.worldgen.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.worldgen.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.worldgen.event.ChunkGenEvent;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkDataBlock extends SimpleExpression<BlockData> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprChunkDataBlock.class, BlockData.class,
                "chunk[ ]data block[data] at %vector%",
                "chunk[ ]data block[data] at %number%, %number%, %number%",
                "chunk[ ]data block[data]s (between|within) %vector% (and|to) %vector%",
                "chunk[ ]data block[data]s (between|within) %number%, %number%, %number% (and|to) %number%, %number%, %number%")
            .name("ChunkGenerator - ChunkData Block")
            .description("Represents blocks in a ChunkData.",
                "The first pattern is used to set a block in a `chunk gen` or `block pop` section.",
                "The second pattern is used to fill blocks between 2 points in a `chunk gen` section.",
                "NOTE: The vector represents the position in a chunk, not a world.",
                "NOTE: You CAN reach into neighbouring chunks going below 0/above 15 in the vector. I don't know how far you can safely reach though.")
            .examples("chunk gen:",
                "\tset chunkdata blocks within vector({_x}, 0, {_z}) and vector({_x}, {_y}, {_z}) to red_concrete[]",
                "\tset chunkdata block at vector({_x}, {_y}, {_z}) to red_concrete_powder[]")
            .since("3.5.0")
            .register();
    }

    private Expression<Vector> vector;
    private Expression<Vector> vector2;
    private Expression<Number> x1, y1, z1;
    private Expression<Number> x2, y2, z2;

    @SuppressWarnings("unchecked")
    @Override

    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (matchedPattern >= 2 && !ParserInstance.get().isCurrentEvent(ChunkGenEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen sections.");
            return false;
        } else if (matchedPattern <= 1 && !ParserInstance.get().isCurrentEvent(ChunkGenEvent.class, BlockPopulateEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen/block pop sections.");
            return false;
        }
        if (matchedPattern == 0) {
            this.vector = (Expression<Vector>) exprs[0];
        } else if (matchedPattern == 1) {
            this.x1 = (Expression<Number>) exprs[0];
            this.y1 = (Expression<Number>) exprs[1];
            this.z1 = (Expression<Number>) exprs[2];
        } else if (matchedPattern == 2) {
            this.vector = (Expression<Vector>) exprs[0];
            this.vector2 = (Expression<Vector>) exprs[1];
        } else if (matchedPattern == 3) {
            this.x1 = (Expression<Number>) exprs[0];
            this.y1 = (Expression<Number>) exprs[1];
            this.z1 = (Expression<Number>) exprs[2];
            this.x2 = (Expression<Number>) exprs[3];
            this.y2 = (Expression<Number>) exprs[4];
            this.z2 = (Expression<Number>) exprs[5];
        }

        return true;
    }

    @Override
    protected BlockData @Nullable [] get(Event event) {
        int blockX;
        int blockY;
        int blockZ;
        if (this.vector != null) {
            Vector vec = this.vector.getSingle(event);
            if (vec == null) {
                return null;
            }
            blockX = vec.getBlockX();
            blockY = vec.getBlockY();
            blockZ = vec.getBlockZ();
        } else {
            Number x1 = this.x1.getSingle(event);
            Number y1 = this.y1.getSingle(event);
            Number z1 = this.z1.getSingle(event);
            if (x1 == null || y1 == null || z1 == null) {
                return null;
            }
            blockX = x1.intValue();
            blockY = y1.intValue();
            blockZ = z1.intValue();
        }

        if (event instanceof ChunkGenEvent chunkGenEvent) {
            return new BlockData[]{chunkGenEvent.getChunkData().getBlockData(blockX, blockY, blockZ)};
        } else if (event instanceof BlockPopulateEvent popEvent) {
            int x = (popEvent.getChunkX() << 4) + blockX;
            int z = (popEvent.getChunkZ() << 4) + blockZ;
            return new BlockData[]{popEvent.getLimitedRegion().getBlockData(x, blockY, z)};
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

            int blockX;
            int blockY;
            int blockZ;
            if (this.vector != null) {
                Vector vec = this.vector.getSingle(event);
                if (vec == null) {
                    return;
                }
                blockX = vec.getBlockX();
                blockY = vec.getBlockY();
                blockZ = vec.getBlockZ();
            } else {
                Number x1 = this.x1.getSingle(event);
                Number y1 = this.y1.getSingle(event);
                Number z1 = this.z1.getSingle(event);
                if (x1 == null || y1 == null || z1 == null) {
                    return;
                }

                blockX = x1.intValue();
                blockY = y1.intValue();
                blockZ = z1.intValue();
            }
            if (event instanceof ChunkGenEvent chunkGenEvent) {
                if (this.vector2 != null || this.x2 != null) {
                    int blockX2;
                    int blockY2;
                    int blockZ2;
                    if (this.vector2 != null) {
                        Vector vec2 = this.vector2.getSingle(event);
                        if (vec2 == null) {
                            return;
                        }
                        blockX2 = vec2.getBlockX();
                        blockY2 = vec2.getBlockY();
                        blockZ2 = vec2.getBlockZ();
                    } else if (this.x2 != null) {
                        Number x2 = this.x2.getSingle(event);
                        Number y2 = this.y2.getSingle(event);
                        Number z2 = this.z2.getSingle(event);
                        if (x2 == null || y2 == null || z2 == null) {
                            return;
                        }
                        blockX2 = x2.intValue();
                        blockY2 = y2.intValue();
                        blockZ2 = z2.intValue();
                    } else {
                        return;
                    }
                    setRegion(chunkGenEvent.getChunkData(), blockX, blockY, blockZ, blockX2, blockY2, blockZ2, blockData);
                } else {
                    chunkGenEvent.getChunkData().setBlock(blockX, blockY, blockZ, blockData);
                }
            } else if (event instanceof BlockPopulateEvent popEvent) {
                int x = (popEvent.getChunkX() << 4) + blockX;
                int z = (popEvent.getChunkZ() << 4) + blockZ;
                LimitedRegion region = popEvent.getLimitedRegion();
                if (region.isInRegion(x, blockY, z)) {
                    // Make sure we are setting within the available region
                    region.setBlockData(x, blockY, z, blockData);
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
        if (this.vector2 != null) {
            String vec = this.vector.toString(e, d);
            return "chunkdata blockdata within " + vec + " and " + this.vector2.toString(e, d);
        } else if (this.x2 != null) {
            String sx1 = this.x1.toString(e, d);
            String sy1 = this.y1.toString(e, d);
            String sz1 = this.z1.toString(e, d);
            String sx2 = this.x2.toString(e, d);
            String sy2 = this.y1.toString(e, d);
            String sz2 = this.z1.toString(e, d);
            return "chunkdata blockdata within " + sx1 + ", " + sy1 + ", " + sz1 + " and " + sx2 + ", " + sy2 + ", " + sz2;
        } else if (this.vector != null) {
            return "chunkdata blockdata at " + this.vector.toString(e, d);
        } else {
            String sx1 = this.x1.toString(e, d);
            String sy1 = this.y1.toString(e, d);
            String sz1 = this.z1.toString(e, d);
            return "chunkdata blockdata at " + sx1 + ", " + sy1 + ", " + sz1;
        }
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
