package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Chunks Within Locations")
@Description("Get a list of all chunks within 2 locations.")
@Examples({"loop all chunks within {_l1} and {_l2}:",
    "refresh all chunks within {_l1} and {_l2}"})
@Since("3.6.1")
public class ExprChunksWithinCuboid extends SimpleExpression<Chunk> {

    static {
        Skript.registerExpression(ExprChunksWithinCuboid.class, Chunk.class, ExpressionType.COMBINED,
            "all chunks within %location% and %location%");
    }

    private Expression<Location> loc1, loc2;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        return true;
    }

    @Override
    protected Chunk @Nullable [] get(Event event) {
        Location loc1 = this.loc1.getSingle(event);
        Location loc2 = this.loc2.getSingle(event);
        if (loc1 == null || loc2 == null) {
            return null;
        }

        World world = loc1.getWorld();
        if (world != loc2.getWorld()) {
            String w1 = loc1.getWorld().getName();
            String w2 = loc2.getWorld().getName();
            error("Both locations have to be in the same world but got " + w1 + " and " + w2);
            return null;
        }

        List<Chunk> chunks = new ArrayList<>();
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX()) >> 4;
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ()) >> 4;
        int maxX = (Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1) >> 4;
        int maxZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1) >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                chunks.add(world.getChunkAt(x, z, false));
            }
        }

        return chunks.toArray(new Chunk[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "all chunks within " + this.loc1.toString(e, d) + " and " + this.loc2.toString(e, d);
    }

}
