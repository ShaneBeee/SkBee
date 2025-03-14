package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Bound - Contains Location")
@Description("Check if a location is within the bounds of a bounding box.")
@Examples({"on break:",
    "\tif location of event-block is within bound with id \"spawn.bound\":",
    "\t\tcancel event", "",
    "on damage of a player:",
    "\tif victim is within bound {spawn}:",
    "\t\tcancel event"})
@Since("1.0.0")
public class CondBoundContainsLocation extends Condition {

    static {
        PropertyCondition.register(CondBoundContainsLocation.class, "[with]in [bound[s]] %bounds%", "locations");
    }

    private Expression<Bound> bounds;
    private Expression<Location> locations;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.bounds = (Expression<Bound>) exprs[1];
        this.locations = (Expression<Location>) exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Location[] locs = this.locations.getArray(event);
        boolean and = this.locations.getAnd();
        return this.bounds.check(event, bound ->
            SimpleExpression.check(locs, bound::isInRegion, isNegated(), and));
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return PropertyCondition.toString(this, PropertyCondition.PropertyType.BE, e, d, locations,
            "in the bound[s] " + this.bounds.toString(e, d));
    }

}
