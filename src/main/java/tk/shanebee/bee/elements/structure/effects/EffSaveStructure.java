package tk.shanebee.bee.elements.structure.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.github.shynixn.structureblocklib.api.enumeration.StructureRestriction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;

import java.io.File;

@Name("Structure Block - Save")
@Description("Save structure block structures. 1.9.4+ ONLY")
@Examples("save structure between {loc1} and {loc2} as \"house\"")
@Since("1.0.0")
public class EffSaveStructure extends Effect {

    private static final String WORLD;
    private static final StructureBlockLibApi STRUCTURE_API = StructureBlockLibApi.INSTANCE;

    static {
        String worldContainer = Bukkit.getWorldContainer().getPath();
        if (worldContainer.equalsIgnoreCase(".")) {
            WORLD = Bukkit.getServer().getWorlds().get(0).getName();
        } else {
            WORLD = worldContainer + File.separator + Bukkit.getServer().getWorlds().get(0).getName();
        }
        Skript.registerEffect(EffSaveStructure.class, "save [structure] between %location% and %location% as %string%");
    }

    @SuppressWarnings("null")
    private Expression<Location> loc1;
    private Expression<Location> loc2;
    private Expression<String> name;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
       loc1 = (Expression<Location>) exprs[0];
       loc2 = (Expression<Location>) exprs[1];
       name = (Expression<String>) exprs[2];
       return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        Location loc1 = this.loc1.getSingle(event);
        Location loc2 = this.loc2.getSingle(event);

        if (loc1 == null || loc2 == null) return;

        World world = loc1.getWorld();
        int x = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        Location low = new Location(world, x, y, z);

        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;

        int x3 = x2 - x;
        int y3 = y2 - y;
        int z3 = z2 - z;
        String name = this.name.getSingle(event);

        STRUCTURE_API.saveStructure(SkBee.getPlugin())
                .restriction(StructureRestriction.UNLIMITED)
                .at(low).sizeX(x3).sizeY(y3).sizeZ(z3)
                .includeEntities(true)
                .saveToWorld(WORLD, "minecraft", name)
                .onException(e -> {
                    Skript.error("Could not save structure: " + name);
                    if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "save structure between " + loc1.toString(e, d) + " and " + loc2.toString(e, d) + " as " + name.toString(e, d);
    }

}
