package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Within Locations")
@Description("Check if a location is within 2 other locations.")
@Examples("if location of player is within {_loc1} and {_loc2}:")
@Since("1.16.0")
public class CondWithinLocations extends Condition {

    static {
        Skript.registerCondition(CondWithinLocations.class,
                "%location% is within %location% and %location%",
                "%location% is(n't| not) within %location% and %location%");
    }

    private Expression<Location> location;
    private Expression<Location> with1, with2;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Util.skript27Warning("is within", "condition");
        setNegated(matchedPattern == 1);
        location = (Expression<Location>) exprs[0];
        with1 = (Expression<Location>) exprs[1];
        with2 = (Expression<Location>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        Location loc = location.getSingle(event);
        Location w1 = with1.getSingle(event);
        Location w2 = with2.getSingle(event);
        if (loc == null || w1 == null || w2 == null) return isNegated();

        return isNegated() != MathUtil.isWithin(loc, w1, w2);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        String l1 = location.toString(e, d);
        String l2 = with1.toString(e, d);
        String l3 = with2.toString(e, d);
        String cond = isNegated() ? " is not " : " is ";

        return l1 + cond + "within " + l2 + " and " + l3;
    }

}
