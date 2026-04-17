package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

public class EffChunkLoad extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    public static void register(Registration reg) {
        reg.newEffect(EffChunkLoad.class,
                // Chunk coords
                "[:async] load chunk at %number%,[ ]%number% (in|of) [world] %world% [ticket:with ticket]",

                // Chunk Key
                "[:async] load chunk with [chunk ]key %number% (in|of) [world] %world% [ticket:with ticket]",

                // Location
                "[:async] load chunk at %location% [ticket:with ticket]",

                // Within Locations
                "[:async] load [all] chunks within %location% and %location% [ticket:with ticket[s]]")
            .name("Chunk - Load")
            .description("Load a chunk.",
                "**Options**:",
                "- `%number%,[ ]%number%` = Represents the X/Z coords of a chunk. Not to be confused with a location. " +
                    "Chunk coords are essentially a location divided by 16, example: Chunk 1/1 = Location 16/16",
                " - `chunk key %number%` = Represents the key of a chunk (chunk's chunk coordinates packed into a long).",
                "- `async` = Will load the chunk off the main thread. Your code will halt whilst waiting for the chunk[s] to load.",
                "- `with ticket` = Will add a ticket to the chunk, preventing it from unloading until you explicitly unload it or the server stops.")
            .examples("load chunk at 1,1 in world \"world\"",
                "load chunk at location(1,1,1, world \"world\")",
                "load chunk at 150,150 in world \"world\"",
                "load chunk at 150,150 in world \"world\" with ticket",
                "load chunks within {_l1} and {_l2}",
                "load chunks within {_l1} and {_l2} with tickets",
                "async load chunk at {_loc}",
                "async load chunk at 100,100 in world \"world\"",
                "async load chunk at 1,1 in world of player with ticket",
                "async load chunks within {_l1} and {_l2} with tickets")
            .since("3.20.0")
            .register();
    }

    private int pattern;
    private boolean hasTicket;
    private boolean isAsync;
    private Expression<Number> x;
    private Expression<Number> z;
    private Expression<Number> key;
    private Expression<World> world;
    private Expression<Location> location;
    private Expression<Location> locationOne;
    private Expression<Location> locationTwo;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parseResult) {
        this.pattern = pattern;
        this.hasTicket = parseResult.hasTag("ticket");
        this.isAsync = parseResult.hasTag("async");

        if (pattern == 0) {
            this.x = (Expression<Number>) exprs[0];
            this.z = (Expression<Number>) exprs[1];
            this.world = (Expression<World>) exprs[2];
        } else if (pattern == 1) {
            this.key = (Expression<Number>) exprs[0];
            this.world = (Expression<World>) exprs[2];
        } else if (pattern == 2) {
            this.location = (Expression<Location>) exprs[0];
        } else if (pattern == 3) {
            this.locationOne = (Expression<Location>) exprs[0];
            this.locationTwo = (Expression<Location>) exprs[1];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        // Walk instead
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        if (this.locationOne != null) {
            Queue<Pair<Integer, Integer>> posQueue = new LinkedList<>();
            Location l1 = this.locationOne.getSingle(event);
            Location l2 = this.locationTwo.getSingle(event);
            if (l1 == null || l2 == null) {
                return next;
            }
            World world = l1.getWorld();

            int minX = Math.min(l1.getBlockX() >> 4, l2.getBlockX() >> 4);
            int minZ = Math.min(l1.getBlockZ() >> 4, l2.getBlockZ() >> 4);
            int maxX = Math.max(l1.getBlockX() >> 4, l2.getBlockX() >> 4);
            int maxZ = Math.max(l1.getBlockZ() >> 4, l2.getBlockZ() >> 4);
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    posQueue.add(new Pair<>(x, z));
                }
            }

            // Let's save you guys for later after the chunk has loaded
            Object localVars = Variables.removeLocals(event);

            // Let's start loading chunks
            loadChunkInQueue(posQueue, world, event, next, localVars);

            return null;
        }

        int x = 0;
        int z = 0;

        World world = Bukkit.getWorlds().getFirst();

        if (this.world != null) {
            world = this.world.getSingle(event);
            if (world == null) {
                return next;
            }
            if (this.key != null) {
                Number keyNum = this.key.getSingle(event);
                if (keyNum == null) {
                    return next;
                }
                long l = keyNum.longValue();
                x = (int) l;
                z = (int) (l >> 32);
            } else if (this.x != null && this.z != null) {
                Number xSingle = this.x.getSingle(event);
                Number zSingle = this.z.getSingle(event);

                if (xSingle == null || zSingle == null) {
                    return next;
                }
                x = xSingle.intValue();
                z = zSingle.intValue();
            }
        }
        if (this.location != null) {
            Location location = this.location.getSingle(event);
            if (location != null) {
                x = location.getBlockX() >> 4;
                z = location.getBlockZ() >> 4;
                world = location.getWorld();
            }
        }

        Queue<Pair<Integer, Integer>> posQueue = new LinkedList<>();
        posQueue.add(new Pair<>(x, z));

        // Let's save you guys for later after the chunk has loaded
        Object localVars = Variables.removeLocals(event);
        loadChunkInQueue(posQueue, world, event, next, localVars);

        return null;
    }

    private void loadChunkInQueue(Queue<Pair<Integer, Integer>> posQueue, World world, Event event, TriggerItem next, Object localVars) {
        if (posQueue.isEmpty()) {
            // re-set local variables
            if (localVars != null) Variables.setLocalVariables(event, localVars);

            // walk next trigger
            if (next != null) TriggerItem.walk(next, event);

            // remove local vars as we're now done
            Variables.removeLocals(event);
        } else {
            Pair<Integer, Integer> pos = posQueue.poll();
            int x = pos.first();
            int z = pos.second();
            if (this.isAsync) {
                world.getChunkAtAsync(x, z).thenAccept(chunk -> {
                    if (this.hasTicket) chunk.addPluginChunkTicket(PLUGIN);
                    loadChunkInQueue(posQueue, world, event, next, localVars);
                });
            } else {
                Chunk chunk = world.getChunkAt(x, z);
                if (this.hasTicket) chunk.addPluginChunkTicket(PLUGIN);
                loadChunkInQueue(posQueue, world, event, next, localVars);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String chunk = switch (this.pattern) {
            case 1 -> "with key " + this.key.toString(e, d) + " in world " + this.world.toString(e, d);
            case 2 -> "at " + this.location.toString(e, d);
            case 3 -> "within " + this.locationOne.toString(e, d) + " and " + this.locationTwo.toString(e, d);
            default -> "at " + this.x.toString(e, d) + ","
                + this.z.toString(e, d) + " in world " + this.world.toString(e, d);
        };
        String async = this.isAsync ? "async" : "";
        String ticket = this.hasTicket ? " with ticket" : "";
        return String.format("%s load chunk %s %s", async, chunk, ticket);
    }

}
