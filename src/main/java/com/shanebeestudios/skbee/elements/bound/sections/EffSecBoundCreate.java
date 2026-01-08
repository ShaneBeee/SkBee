package com.shanebeestudios.skbee.elements.bound.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundCreateEvent;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprLastCreatedBound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("Bound - Create")
@Description({"Create a bound within 2 locations. This can be used as an effect and as a section.",
    "Optional value `temporary` creates a bound which persists until server stops (not saved to file).",
    "Optional value `full` will mark the bound to use the world's min/max height.",
    "These optional values can be used together.",
    "**SPECIAL NOTE**:",
    "- When using locations = The bound created will use the locations you pass thru",
    "- When using blocks = The bound created will extend the x/y/z axes by 1 to fully include those blocks."})
@Examples({"# Create bounds using locations",
    "create bound with id \"le-test\" between {_1} and {_2}:",
    "\tset bound value \"le-value\" of event-bound to 52",
    "\tset owner of event-bound to player",
    "create a new bound with id \"%uuid of player%.home\" between {loc1} and {loc2}",
    "create a temporary bound with id \"%{_world}%.safezone-%random uuid%\" between {loc1} and {loc2}",
    "create a full bound with id \"spawn\" between {loc} and location of player",
    "",
    "# Create bounds using blocks",
    "create bound with id \"my_home\" within block at player and block 10 above target block",
    "create bound with id \"le_bound\" within {_block1} and {_block2}",
    "create a new bound with id \"%uuid of player%.home\" between {block1} and {block2}"})
@Since("2.5.3, 2.10.0 (temporary bounds)")
public class EffSecBoundCreate extends EffectSection {

    private static final BoundConfig BOUND_CONFIG = SkBee.getPlugin().getBoundConfig();

    static {
        Skript.registerSection(EffSecBoundCreate.class,
            "create [a] [new] [:temporary] [:full] bound with id %string% (within|between) " +
                "%block/location% and %block/location%");
    }

    private Expression<String> boundID;
    private Expression<?> point1, point2;
    private boolean isFull;
    private boolean isTemporary;
    private Trigger trigger;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.boundID = (Expression<String>) exprs[0];
        this.point1 = exprs[1];
        this.point2 = exprs[2];
        this.isFull = parseResult.hasTag("full");
        this.isTemporary = parseResult.hasTag("temporary");
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
            trigger = loadCode(sectionNode, "bound create", null, afterLoading, BoundCreateEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be within a Create Bound section.");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);

        String id = this.boundID.getSingle(event);
        if (id == null) return super.walk(event, false);


        if (BOUND_CONFIG.boundExists(id)) {
            Util.skriptError("&cBound with id '%s' already exists, cannot overwrite!", id);
            return super.walk(event, false);
        }

        Object point1 = this.point1.getSingle(event);
        Object point2 = this.point2.getSingle(event);
        if (point1 == null || point2 == null) return super.walk(event, false);

        boolean usingBlocks = false;
        Location lesser;
        Location greater;
        if (point1 instanceof Location loc1) lesser = loc1;
        else if (point1 instanceof Block block1) {
            lesser = block1.getLocation();
            usingBlocks = true;
        } else return super.walk(event, false);

        if (point2 instanceof Location loc2) {
            greater = loc2;
            usingBlocks = false;
        } else if (point2 instanceof Block block2) {
            greater = block2.getLocation();
        } else return super.walk(event, false);

        // both locations need to be in the same world
        World worldL = lesser.getWorld();
        World worldG = greater.getWorld();
        if (worldL == null || worldG == null) return super.walk(event, false);

        if (worldL != worldG) {
            String l = Classes.toString(worldL);
            String g = Classes.toString(worldG);
            Util.skriptError("&cBounding box locations must be in the same world, but found &7'&b%s&7' &cand &7'&b%s&7' (&6%s&7)",
                l, g, toString(event, true));
            return super.walk(event, false);
        }

        Bound bound = new Bound(lesser, greater, id, this.isTemporary, usingBlocks);
        bound.setFull(this.isFull);
        BoundingBox box = bound.getBoundingBox();
        if (box.getWidthX() < 1 || box.getWidthZ() < 1 || (box.getHeight() < 1 && !this.isFull)) {
            Util.skriptError("&cBound must have a size of at least 2x2x2 &7(&6%s&7)", toString(event, true));
            return super.walk(event, false);
        }
        ExprLastCreatedBound.lastCreated = bound;

        if (this.trigger != null) {
            BoundCreateEvent boundCreateEvent = new BoundCreateEvent(bound);
            Variables.setLocalVariables(boundCreateEvent, localVars);
            TriggerItem.walk(trigger, boundCreateEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(boundCreateEvent));
            Variables.removeLocals(boundCreateEvent);
        }
        BOUND_CONFIG.saveBound(bound, true);
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String temporary = this.isTemporary ? "temporary " : "";
        String full = this.isFull ? "full " : "";
        String points = " between " + this.point1.toString(e, d) + " and " + this.point2.toString(e, d);
        return "create " + temporary + full + "bound with id " + this.boundID.toString(e, d) + points;
    }

}
