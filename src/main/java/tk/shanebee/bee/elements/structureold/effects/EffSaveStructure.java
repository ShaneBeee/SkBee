package tk.shanebee.bee.elements.structureold.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.github.shynixn.structureblocklib.api.enumeration.StructureRestriction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.util.Util;

import java.io.File;

@Name("Structure Block - Save")
@Description({"Save structure block structures.",
        "Requires Minecraft 1.9.4+. No longer available on MC 1.18+, please use new structure system."})
@Examples("save structure between {loc1} and {loc2} as \"house\"")
@Since("1.0.0")
@SuppressWarnings("NullableProblems")
public class EffSaveStructure extends Effect {

    private static final String WORLD;
    private static final StructureBlockLibApi STRUCTURE_API = StructureBlockLibApi.INSTANCE;
    private static final boolean HAS_NEW_STRUCTURE_API = Skript.classExists("org.bukkit.structure.Structure");

    static {
        String worldContainer = Bukkit.getWorldContainer().getPath();
        String worldName = Bukkit.getWorlds().get(0).getName();
        if (worldContainer.equalsIgnoreCase(".")) {
            WORLD = worldName;
        } else {
            WORLD = worldContainer + File.separator + worldName;
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
        if (HAS_NEW_STRUCTURE_API) {
            Util.skriptError("This effect is deprecated and will be removed in the future. Please use the new structure system.");
        }
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        // Don't need this since we're walking
    }

    @Nullable
    @Override
    protected TriggerItem walk(Event event) {
        debug(event, true);
        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);


        Location loc1 = this.loc1.getSingle(event);
        Location loc2 = this.loc2.getSingle(event);

        if (loc1 == null || loc2 == null) {
            return next;
        }

        World world = loc1.getWorld();
        int x = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        Location low = new Location(world, x, y, z);

        int xDiff = (Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1) - x;
        int yDiff = (Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1) - y;
        int zDiff = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1) - z;

        String name = this.name.getSingle(event);

        Object localVars = Variables.removeLocals(event);
        STRUCTURE_API.saveStructure(SkBee.getPlugin())
                .restriction(StructureRestriction.UNLIMITED)
                .at(low).sizeX(xDiff).sizeY(yDiff).sizeZ(zDiff)
                .includeEntities(true)
                .saveToWorld(WORLD, "minecraft", name)
                .onException(e -> {
                    Skript.error("Could not save structure: " + name);
                    if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                        e.printStackTrace();
                    }
                    continueWalk(next, event, localVars);
                }).onResult(e -> {
                    continueWalk(next, event, localVars);
                }
        );
        return null;
    }

    private void continueWalk(@Nullable TriggerItem next, Event event, Object localVars) {
        if (localVars != null) {
            Variables.setLocalVariables(event, localVars);
        }
        Object timing = null;
        if (next != null) {
            if (SkriptTimings.enabled()) {
                Trigger trigger = getTrigger();
                if (trigger != null) {
                    timing = SkriptTimings.start(trigger.getDebugLabel());
                }
            }
            TriggerItem.walk(next, event);
        }
        Variables.removeLocals(event);
        SkriptTimings.stop(timing);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("save structure between %s and %s as %s",
                loc1.toString(e, d),
                loc2.toString(e, d),
                name.toString(e, d));
    }

}
