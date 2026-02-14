package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class CondBoundContainsLocation extends Condition {

    public static void register(Registration reg) {
        PropertyCondition.register(CondBoundContainsLocation.class, "[with]in [bound[s]] %bounds%", "locations");
        reg.newCondition(CondBoundContainsLocation.class,
                "%locations% (is|are) [with]in [bound[s]] %bounds%",
                "%locations% (isn't|is not|aren't|are not) [with]in [bound[s]] %bounds%")
            .name("Bound - Contains Location")
            .description("Check if a location is within the bounds of a bounding box.")
            .examples("on break:",
                "\tif location of event-block is within bound with id \"spawn.bound\":",
                "\t\tcancel event", "",
                "on damage of a player:",
                "\tif victim is within bound {spawn}:",
                "\t\tcancel event")
            .since("1.0.0")
            .register();
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
