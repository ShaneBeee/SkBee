package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBlockInChunk extends SimpleExpression<Block> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBlockInChunk.class, Block.class,
                "block at %number%,[ ]%number%,[ ]%number% (in|of) %chunk%")
            .name("Block in Chunk")
            .description("Represents a block in a chunk. X/Z coords will be a value from 0 to 15.")
            .examples("set block at 1,1,1 in chunk at player to stone",
                "set {_b} to block at 8,64,8 in chunk at player")
            .since("1.13.0")
            .register();
    }

    private Expression<Chunk> chunk;
    private Expression<Number> x, y, z;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.x = (Expression<Number>) exprs[0];
        this.y = (Expression<Number>) exprs[1];
        this.z = (Expression<Number>) exprs[2];
        this.chunk = (Expression<Chunk>) exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Block @Nullable [] get(Event e) {
        Chunk chunk = this.chunk.getSingle(e);
        if (chunk == null) return null;

        Number xN = this.x.getSingle(e);
        Number yN = this.y.getSingle(e);
        Number zN = this.z.getSingle(e);

        if (xN == null || yN == null || zN == null) {
            return null;
        }

        int x = xN.intValue();
        int y = yN.intValue();
        int z = zN.intValue();
        if (x < 0 || x > 15 || z < 0 || z > 15) {
            return null;
        }

        World world = chunk.getWorld();
        if (y < world.getMinHeight() || y > (world.getMaxHeight() - 1)) {
            return null;
        }
        return new Block[]{chunk.getBlock(x, y, z)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return String.format("Block at %s,%s,%s in chunk %s",
            this.x.toString(e, d),
            this.y.toString(e, d),
            this.z.toString(e, d),
            this.chunk.toString(e, d));
    }

}
