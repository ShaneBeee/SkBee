package tk.shanebee.bee.elements.structure.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.elements.structure.StructureBee;

@Name("Structure - Fill")
@Description("Fill a structure with blocks.")
@Examples({"fill structure {_s} between {loc1} and {loc2}",
        "fill structure {_s} between location at player and location(10,10,10, world \"world\")"})
@Since("1.12.0")
public class EffStructureFill extends Effect {

    static {
        if (Skript.classExists("org.bukkit.structure.Structure")) {
            Skript.registerEffect(EffStructureFill.class, "fill [structure] %structure% between %location% and %location%");
        }
    }

    private Expression<StructureBee> structure;
    private Expression<Location> loc1;
    private Expression<Location> loc2;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        structure = (Expression<StructureBee>) exprs[0];
        loc1 = (Expression<Location>) exprs[1];
        loc2 = (Expression<Location>) exprs[2];
        return true;
    }

    @Override
    protected void execute(Event e) {
        StructureBee structure = this.structure.getSingle(e);
        Location loc1 = this.loc1.getSingle(e);
        Location loc2 = this.loc2.getSingle(e);

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

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return String.format("fill structure %s between %s and %s",
                structure.toString(e, d), loc1.toString(e,d), loc2.toString(e,d));
    }

}
