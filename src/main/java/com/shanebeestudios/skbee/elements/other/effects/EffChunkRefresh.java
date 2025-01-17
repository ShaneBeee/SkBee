package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Refresh Chunk")
@Description({"Resends chunks to the clients. This is useful if you change a biome and need to refresh the client.",
    "The two numbers represent a chunk's X/Y coords, NOT a location. A chunk's X/Y coords are basically",
    "a location divided by 16. Ex: Chunk 1/1 would be at location X=16, Z=16."})
@Examples({"set biome of block at player to plains",
    "refresh chunk at player",
    "refresh chunk at 1,1 in world \"world\""})
@Since("3.4.0")
public class EffChunkRefresh extends Effect {

    static {
        Skript.registerEffect(EffChunkRefresh.class,
            "refresh [chunk[s]] %chunks%",
            "refresh chunk at %number%,[ ]%number% (in|of) [world] %world%");
    }

    private int pattern;
    private Expression<Chunk> chunks;
    private Expression<Number> x, z;
    private Expression<World> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (matchedPattern == 0) {
            this.chunks = (Expression<Chunk>) exprs[0];
        } else {
            this.x = (Expression<Number>) exprs[0];
            this.z = (Expression<Number>) exprs[1];
            this.world = (Expression<World>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (this.pattern == 0) {
            for (Chunk chunk : this.chunks.getArray(event)) {
                World world = chunk.getWorld();
                world.refreshChunk(chunk.getX(), chunk.getZ());
            }
        } else {
            Number x = this.x.getSingle(event);
            Number z = this.z.getSingle(event);
            World world = this.world.getSingle(event);
            if (x == null) {
                error("X is not set: " + this.x.toString(event, true));
                return;
            }
            if (z == null) {
                error("Z is not set: " + this.z.toString(event, true));
                return;
            }
            if (world == null) {
                error("World is not set: " + this.world.toString(event, true));
                return;
            }
            world.refreshChunk(x.intValue(), z.intValue());
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        if (this.pattern == 0) {
            return "refresh chunk[s] " + this.chunks.toString(e, d);
        }
        String x = this.x.toString(e, d);
        String z = this.z.toString(e, d);
        String w = this.world.toString(e, d);
        return String.format("refresh chunk at %s,%s in %s", x, z, w);
    }

}
