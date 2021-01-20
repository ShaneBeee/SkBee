package tk.shanebee.bee.elements.structure.effects;

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
import com.github.shynixn.structureblocklib.api.enumeration.StructureMirror;
import com.github.shynixn.structureblocklib.api.enumeration.StructureRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.SkBee;

import java.io.File;

@Name("Structure Block - Load")
@Description("Load structure block structures that are saved on your server. " +
        "Optional values for rotation, mirroring and the inclusion of entities. Requires Minecraft 1.9.4+")
@Examples({"load \"house\" at location of player", "load \"barn\" at location 10 infront of player",
        "paste \"house\" at location of player with rotation 90 and with mirror left to right",
        "load \"sheep_pen\" at location below player with rotation 180 and with entities"})
@Since("1.0.0")
@SuppressWarnings("NullableProblems")
public class EffLoadStructure extends Effect {

    private static final String WORLD;
    private static final StructureBlockLibApi STRUCTURE_API = StructureBlockLibApi.INSTANCE;

    static {
        String worldContainer = Bukkit.getWorldContainer().getPath();
        String worldName = Bukkit.getWorlds().get(0).getName();
        if (worldContainer.equalsIgnoreCase(".")) {
            WORLD = worldName;
        } else {
            WORLD = worldContainer + File.separator + worldName;
        }
        Skript.registerEffect(EffLoadStructure.class,
                "(load|paste) [structure] %string% at %location% [with rotation (0¦0|1¦90|2¦180|3¦270)] [(|5¦[and] with entities)]",
                "(load|paste) [structure] %string% at %location% [with rotation (0¦0|1¦90|2¦180|3¦270)] [and] [with] mirror front to back [(|5¦[and] with entities)]",
                "(load|paste) [structure] %string% at %location% [with rotation (0¦0|1¦90|2¦180|3¦270)] [and] [with] mirror left to right [(|5¦[and] with entities)]");
    }

    @SuppressWarnings("null")
    private Expression<String> name;
    private Expression<Location> loc;
    private int rotate = 0;
    private int mirror;
    private boolean withEntities;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int i, @NotNull Kleenean kleenean, ParseResult parseResult) {
        name = (Expression<String>) exprs[0];
        loc = (Expression<Location>) exprs[1];
        rotate = parseResult.mark;
        mirror = i;
        withEntities = rotate == 5 || rotate == 4 || rotate == 7 || rotate == 6;
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

        StructureRotation rotation;
        switch (this.rotate) {
            case 1:
            case 4:
                rotation = StructureRotation.ROTATION_90;
                break;
            case 2:
            case 7:
                rotation = StructureRotation.ROTATION_180;
                break;
            case 3:
            case 6:
                rotation = StructureRotation.ROTATION_270;
                break;
            default:
                rotation = StructureRotation.NONE;
        }

        StructureMirror mirror;
        switch (this.mirror) {
            case 1:
                mirror = StructureMirror.FRONT_BACK;
                break;
            case 2:
                mirror = StructureMirror.LEFT_RIGHT;
                break;
            default:
                mirror = StructureMirror.NONE;
        }

        boolean debug = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;
        Location location = loc.getSingle(event);
        if (location == null) {
            if (debug) {
                Skript.error("Could not load structure " + name.toString(event, true) +
                        " .. location does not exist: " + loc.toString(event, true));
            }
            return next;
        }
        String name = this.name.getSingle(event);

        Object localVars = Variables.removeLocals(event);
        STRUCTURE_API.loadStructure(SkBee.getPlugin())
                .at(location).rotation(rotation).mirror(mirror)
                .includeEntities(withEntities)
                .loadFromWorld(WORLD, "minecraft", name)
                .onException(e -> {
                    Skript.error("Structure " + this.name.toString(event, true) + " does not exist!");
                    if (debug) {
                        e.printStackTrace();
                    }
                }).onResult(c -> {
                    if (localVars != null) {
                        Variables.setLocalVariables(event, localVars);
                    }
                    continueWalk(next, event);
                }
        );

        return null;
    }

    private void continueWalk(@Nullable TriggerItem next, Event event) {
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
    public @NotNull String toString(Event e, boolean d) {
        return "load structure " + name.toString(e, d) + " at " + loc.toString(e, d);
    }

}
