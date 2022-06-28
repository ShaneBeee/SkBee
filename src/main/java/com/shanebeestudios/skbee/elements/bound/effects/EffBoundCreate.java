package com.shanebeestudios.skbee.elements.bound.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.WorldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.jetbrains.annotations.NotNull;

@Name("Bound - Create/Remove")
@Description("Create/Remove a bound with id between 2 locations. " +
        "Optional value \"full\" is a bound from min to max height of world.")
@Examples({"create a new bound with id \"%uuid of player%.home\" between {loc1} and {loc2}",
        "create a full bound with id \"spawn\" between {loc} and location of player",
        "delete bound with id \"my.home\""})
@Since("1.0.0")
public class EffBoundCreate extends Effect {

    private static final BoundConfig boundConfig;

    static {
        boundConfig = SkBee.getPlugin().getBoundConfig();
        Skript.registerEffect(EffBoundCreate.class,
                "create [a] [new] [(1¦full)] bound with id %string% (within|between) %location% and %location%",
                "(delete|remove) bound with id %string%");
    }

    @SuppressWarnings("null")
    private Expression<String> id;
    private Expression<Location> loc1, loc2;
    private boolean create;
    private boolean full;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        this.create = matchedPattern == 0;
        if (create) {
            this.full = parseResult.mark == 1;
            this.loc1 = (Expression<Location>) exprs[1];
            this.loc2 = (Expression<Location>) exprs[2];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        String id = this.id.getSingle(event);
        if (id == null) return;

        if (create) {
            if (boundConfig.boundExists(id)) {
                Util.skriptError("&cBound with id '%s' already exists, cannot overwrite!", id);
                return;
            }

            Location lesser = this.loc1.getSingle(event);
            Location greater = this.loc2.getSingle(event);
            if (lesser == null || greater == null) return;

            // both locations need to be in the same world
            World worldL = lesser.getWorld();
            World worldG = greater.getWorld();
            if (worldL == null || worldG == null) return;

            if (worldL != worldG) {
                String l = Classes.toString(worldL);
                String g = Classes.toString(worldG);
                Util.skriptError("&cBounding box locations must be in the same world, but found &7'&b%s&7' &cand &7'&b%s&7' (&6%s&7)",
                        l, g, toString(event, true));
                return;
            }

            if (full) {
                // clone to prevent changing original location variables
                lesser = lesser.clone();
                greater = greater.clone();
                int max = worldG.getMaxHeight() - 1;
                int min = WorldUtils.getMinHeight(worldG);

                lesser.setY(min);
                greater.setY(max);
            }
            Bound bound = new Bound(lesser, greater, id);
            if (bound.getGreaterY() - bound.getLesserY() < 1 ||
                    bound.getGreaterX() - bound.getLesserX() < 1 ||
                    bound.getGreaterZ() - bound.getLesserZ() < 1) {
                Util.skriptError("&cBounding box must have a size of at least 2x2x2 &7(&6%s&7)", toString(event, true));
                return;
            }
            boundConfig.saveBound(bound);
        } else {
            Bound bound = boundConfig.getBoundFromID(id);
            if (bound != null)
                boundConfig.removeBound(bound);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = this.create ? "create" : "delete";
        String full = this.full ? " full " : " ";
        String create = this.create ? " between " + loc1.toString(e, d) + " and " + loc2.toString(e, d) : "";
        return type + full + "bound with id " + id.toString(e, d) + create;
    }

}
