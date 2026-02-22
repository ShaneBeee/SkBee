package com.shanebeestudios.skbee.elements.structure.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.BlockVector;

public class EffStructureFill extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffStructureFill.class,
                "fill [structure] %structure% (between|within) %location% and %location%")
            .name("Structure - Fill")
            .description("Fill a structure with blocks.")
            .examples("set {_s} to structure with id \"my_structure\"",
                "fill structure {_s} between {loc1} and {loc2}",
                "fill structure {_s} between location at player and location(10,10,10, world \"world\")")
            .since("1.12.0")
            .register();
    }

    private Expression<StructureWrapper> structure;
    private Expression<Location> loc1;
    private Expression<Location> loc2;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        structure = (Expression<StructureWrapper>) exprs[0];
        loc1 = (Expression<Location>) exprs[1];
        loc2 = (Expression<Location>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        StructureWrapper structure = this.structure.getSingle(event);
        Location loc1 = this.loc1.getSingle(event);
        Location loc2 = this.loc2.getSingle(event);

        if (structure == null || loc1 == null || loc2 == null) {
            return;
        }

        // Both locations must be in the same world
        if (loc1.getWorld() != loc2.getWorld()) {
            return;
        }
        World world = loc1.getWorld();
        int x = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        Location low = new Location(world, x, y, z);

        int xDiff = (Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1) - x;
        int yDiff = (Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1) - y;
        int zDiff = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1) - z;

        BlockVector offset = new BlockVector(xDiff, yDiff, zDiff);

        structure.fill(low, offset);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(Event e, boolean d) {
        return String.format("fill structure %s between %s and %s",
            structure.toString(e, d), loc1.toString(e, d), loc2.toString(e, d));
    }

}
