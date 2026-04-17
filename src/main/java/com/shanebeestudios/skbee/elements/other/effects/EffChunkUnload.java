package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffChunkUnload extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    public static void register(Registration reg) {
        reg.newEffect(EffChunkUnload.class,
                // Chunk coords
                "unload chunk at %number%,[ ]%number% (in|of) [world] %world% [nosave:without saving]",

                // Location
                "unload chunk at %location% [nosave:without saving]",

                // Chunk
                "unload %chunks% [nosave:without saving]")
            .name("Chunk - Unload")
            .description("Unload a chunk.",
                "This will remove any chunk tickets that were holding the chunk open.",
                "**Options**:",
                " - `%number%,[ ]%number%` = Represents the X/Z coords of a chunk. Not to be confused with a location. " +
                    "Chunk coords are essentially a location divided by 16, example: Chunk 1/1 = Location 16/16",
                " - `without saving` = Will prevent the chunk from saving when unloading.")
            .examples("unload chunk at 1,1 in world \"world\"",
                "unload chunk at 1,1 in world \"world\" without saving",
                "unload chunk at {_loc}")
            .since("INSERT VERSION")
            .register();
    }

    private int pattern;
    private boolean save;
    private Expression<Number> x;
    private Expression<Number> z;
    private Expression<World> world;
    private Expression<Location> location;
    private Expression<Chunk> chunks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.pattern = pattern;
        this.save = !parseResult.hasTag("nosave");

        if (pattern == 0) {
            this.x = (Expression<Number>) exprs[0];
            this.z = (Expression<Number>) exprs[1];
            this.world = (Expression<World>) exprs[2];
        } else if (pattern == 1) {
            this.location = (Expression<Location>) exprs[0];
        } else {
            this.chunks = (Expression<Chunk>) exprs[0];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (this.pattern < 2) {
            int x = 0;
            int z = 0;

            World world = Bukkit.getWorlds().getFirst();

            if (this.x != null && this.z != null && this.world != null) {
                Number xSingle = this.x.getSingle(event);
                Number zSingle = this.z.getSingle(event);
                world = this.world.getSingle(event);
                if (xSingle == null || zSingle == null || world == null) {
                    return;
                }
                x = xSingle.intValue();
                z = zSingle.intValue();
            }
            if (this.location != null) {
                Location location = this.location.getSingle(event);
                if (location != null) {
                    x = location.getBlockX() >> 4;
                    z = location.getBlockZ() >> 4;
                    world = location.getWorld();
                }
            }

            world.removePluginChunkTicket(x, z, PLUGIN);
            world.unloadChunk(x, z, this.save);
        } else {
            for (Chunk chunk : this.chunks.getArray(event)) {
                chunk.removePluginChunkTicket(PLUGIN);
                chunk.unload(this.save);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String chunk = switch (this.pattern) {
            case 0 -> "at " + this.location.toString(e, d);
            case 2 -> this.chunks.toString(e, d);
            default -> "at " + this.x.toString(e, d) + "," + this.z.toString(e, d);
        };
        return String.format("unload chunk %s", chunk);
    }
}
