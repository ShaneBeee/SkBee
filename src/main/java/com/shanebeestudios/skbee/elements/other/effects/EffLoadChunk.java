package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Chunk - Load/Unload")
@Description({"Load or unload a chunk. When loading you have an option to add a ticket.",
        "This will prevent the chunk from unloading until you explicitly unload it, or the server stops.",
        "The two numbers represent a chunk's X/Y coords, NOT a location. A chunk's X/Y coords are basically",
        "a location divided by 16. Ex: Chunk 1/1 would be at X=16, Z=16.",
        "NOTE: If no ticket is added, and the chunk has no players to keep it active, it will immediately unload.",
        "NOTE: When adding a ticket, a bunch of chunks will load in a radius around said chunk."})
@Examples({"load chunk at 1,1 in world \"world\"",
        "unload chunk at 1,1 in world \"world\"",
        "load chunk chunk at location(1,1,1, world \"world\")",
        "load chunk at 150,150 in world \"world\"",
        "load chunk at 150,150 in world \"world\" with ticket"})
@Since("INSERT VERSION")
public class EffLoadChunk extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    static {
        Skript.registerEffect(EffLoadChunk.class,
                "(load|1¦unload) chunk at %number%,[ ]%number% (in|of) [world] %world%",
                "(load|1¦unload) chunk %chunk%",
                "load chunk at %number%,[ ]%number% (in|of) [world] %world% with ticket",
                "load chunk %chunk% with ticket");
    }

    private int pattern;
    private boolean ticket;
    private boolean unload;
    private Expression<Number> one;
    private Expression<Number> two;
    private Expression<World> world;
    private Expression<Chunk> chunk;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.pattern = i;
        this.unload = parseResult.mark == 1;
        this.ticket = i > 1;

        if (i == 0 || i == 2) {
            this.chunk = null;
            this.one = (Expression<Number>) exprs[0];
            this.two = (Expression<Number>) exprs[1];
            this.world = (Expression<World>) exprs[2];
        } else {
            this.chunk = (Expression<Chunk>) exprs[0];
            this.one = null;
            this.two = null;
            this.world = null;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        //Chunk chunk = null;
        if (pattern == 0 || pattern == 2) {
            int one = 0;
            int two = 0;
            World world = Bukkit.getWorlds().get(0);

            if (this.one != null) {
                Number oneSingle = this.one.getSingle(event);
                if (oneSingle != null) one = oneSingle.intValue();
            }
            if (this.two != null) {
                Number twoSingle = this.two.getSingle(event);
                if (twoSingle != null) two = twoSingle.intValue();
            }
            if (this.world != null) {
                world = this.world.getSingle(event);
            }
            if (world == null) return;

            if (unload) {
                world.removePluginChunkTicket(one, two, PLUGIN);
                world.unloadChunk(one, two);
            } else {
                world.loadChunk(one, two);
                if (ticket) {
                    world.addPluginChunkTicket(one, two, PLUGIN);
                }
            }
        } else {
            if (this.chunk != null) {
                Chunk chunk = this.chunk.getSingle(event);
                if (chunk == null) return;
                if (unload) {
                    chunk.removePluginChunkTicket(PLUGIN);
                    chunk.unload();
                } else {
                    chunk.load(); // wouldn't the chunk already be loaded?!?!
                    if (ticket) {
                        chunk.addPluginChunkTicket(PLUGIN);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String load = this.unload ? "unload" : "load";
        String chunk = (pattern == 0 || pattern == 2) ? "at " + this.one.toString(e, d) + "," +
                this.two.toString(e,d) : this.chunk.toString(e,d);
        String ticket = this.ticket ? " with ticket" : "";
        return String.format("%s chunk %s %s",
                load, chunk, ticket);
    }

}
