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
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.util.WorldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("Bound - Create")
@Description({"Create a bound within 2 locations. This can be used as an effect and as a section.",
        "\tNote: optional `[full]` will create a bound from min to max height of world.",
        "\nNOTE: optional `[blocks]` will force the bound to wrap around the block at the greater corner.",
        "If not using this, the bound will be created at the exact location you input and may exclude blocks."})
@Examples({"create bound with id \"le-test\" between {_1} and {_2}:",
        "\tset bound value \"le-value\" of event-bound to 52",
        "\tset owner of event-bound to player",
        "create a new bound with id \"%uuid of player%.home\" within blocks at {loc1} and {loc2}",
        "create a new bound with id \"%uuid of player%.home\" within blocks {block1} and {block2}",
        "create a full bound with id \"spawn\" between {loc} and location of player"})
@Since("2.5.3")
public class EffSecBoundCreate extends EffectSection {

    // Bound event for event-bound in section
    public static class BoundCreateEvent extends Event {

        private final Bound bound;

        public BoundCreateEvent(Bound bound) {
            this.bound = bound;
        }

        public Bound getBound() {
            return bound;
        }

        @Override
        @NotNull
        public HandlerList getHandlers() {
            throw new IllegalStateException();
        }

    }

    private static final BoundConfig boundConfig;

    static {
        boundConfig = SkBee.getPlugin().getBoundConfig();
        Skript.registerSection(EffSecBoundCreate.class,
                "create [a] [new] [:full] bound with id %string% (within|between) " +
                        "[:blocks[ at]] %block/location% and %block/location%");

        EventValues.registerEventValue(BoundCreateEvent.class, Bound.class, new Getter<>() {
            @Override
            public @Nullable Bound get(BoundCreateEvent event) {
                return event.getBound();
            }
        }, 0);
    }

    private Expression<String> boundID;
    private Expression<Object> loc1, loc2;
    private boolean isFull;
    private Trigger trigger;
    private boolean blocks;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        this.boundID = (Expression<String>) exprs[0];
        this.loc1 = (Expression<Object>) exprs[1];
        this.loc2 = (Expression<Object>) exprs[2];
        this.isFull = parseResult.hasTag("full");
        this.blocks = parseResult.hasTag("blocks");
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            trigger = loadCode(sectionNode, "bound create", BoundCreateEvent.class);
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


        if (boundConfig.boundExists(id)) {
            Util.skriptError("&cBound with id '%s' already exists, cannot overwrite!", id);
            return super.walk(event, false);
        }

        Object loc1 = this.loc1.getSingle(event);
        Object loc2 = this.loc2.getSingle(event);
        if (loc1 == null || loc2 == null) return super.walk(event, false);

        Location lesser = null;
        Location greater = null;
        if (loc1 instanceof Location loc) {
            lesser = loc;
        } else if (loc1 instanceof Block block) {
            lesser = block.getLocation();
        }
        if (loc2 instanceof Location loc) {
            greater = loc;
        } else if (loc2 instanceof Block block) {
            greater = block.getLocation();
        }

        if (lesser == null || greater == null) return super.walk(event, false);

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

        if (isFull) {
            // clone to prevent changing original location variables
            lesser = lesser.clone();
            greater = greater.clone();
            int max = WorldUtils.getMaxHeight(worldG);
            int min = WorldUtils.getMinHeight(worldG);

            lesser.setY(min);
            greater.setY(max);
        }
        Bound bound;
        if (this.blocks) {
            bound = new Bound(lesser.getBlock(), greater.getBlock(), id);
        } else {
            bound = new Bound(lesser, greater, id);
        }
        if (bound.getGreaterY() - bound.getLesserY() < 1 ||
                bound.getGreaterX() - bound.getLesserX() < 1 ||
                bound.getGreaterZ() - bound.getLesserZ() < 1) {
            Util.skriptError("&cBounding box must have a size of at least 2x2x2 &7(&6%s&7)", toString(event, true));
            return super.walk(event, false);
        }
        boundConfig.saveBound(bound);

        if (trigger != null) {
            BoundCreateEvent boundCreateEvent = new BoundCreateEvent(bound);
            Variables.setLocalVariables(boundCreateEvent, localVars);
            TriggerItem.walk(trigger, boundCreateEvent);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(boundCreateEvent));
            Variables.removeLocals(boundCreateEvent);
        }
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String full = this.isFull ? " full " : " ";
        String blocks = this.blocks ? " blocks " : "";
        String create = " within " + blocks + this.loc1.toString(e, d) + " and " + this.loc2.toString(e, d);
        return "create" + full + "bound with id " + this.boundID.toString(e, d) + create;
    }

}
