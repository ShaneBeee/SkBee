package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprChunkKey extends SimpleExpression<Long> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprChunkKey.class, Long.class,
                "chunk key of %chunk%",
                "chunk key at %location%",
                "chunk key at %number%,[ ]%number%")
            .name("Chunk - ChunkKey")
            .description("Returns a chunk's chunk coordinates packed into a long.",
                "Example would be chunk at 0,0 = 0, chunk at 1,1 = 4294967297.",
                "This can be thought of an ID for chunks.")
            .register();
    }

    private int pattern;
    private Expression<Chunk> chunk;
    private Expression<Location> location;
    private Expression<Number> x, z;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (this.pattern == 0) {
            this.chunk = (Expression<Chunk>) expressions[0];
        } else if (this.pattern == 1) {
            this.location = (Expression<Location>) expressions[0];
        } else {
            this.x = (Expression<Number>) expressions[0];
            this.z = (Expression<Number>) expressions[1];
        }
        return true;
    }

    @Override
    protected Long @Nullable [] get(Event event) {
        if (this.pattern == 0) {
            Chunk chunk = this.chunk.getSingle(event);
            if (chunk != null) {
                return new Long[]{chunk.getChunkKey()};
            }
        } else if (this.pattern == 1) {
            Location loc = this.location.getSingle(event);
            if (loc != null) {
                return new Long[]{Chunk.getChunkKey(loc)};
            }
        } else {
            Number xNum = this.x.getSingle(event);
            Number zNum = this.z.getSingle(event);
            if (xNum != null && zNum != null) {
                return new Long[]{Chunk.getChunkKey(xNum.intValue(), zNum.intValue())};
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (this.pattern == 0) {
            return "chunk key of " + this.chunk.toString(event, debug);
        } else if (this.pattern == 1) {
            return "chunk key at " + this.location.toString(event, debug);
        }
        return "chunk key at " + this.x.toString(event, debug) + ", " + this.z.toString(event, debug);
    }

}
