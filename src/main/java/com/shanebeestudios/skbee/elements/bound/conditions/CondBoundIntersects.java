package com.shanebeestudios.skbee.elements.bound.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Bound - Intersects")
@Description("Check if a bound intersects with another bound or a potential bound at 2 locations.")
@Examples({"if {_bound} intersects with {_loc1} and {_loc2}:",
        "if {_bound} intersects with {_bound2}:",
        "if {_bound} doesn't intersect with {_loc1} and {_loc2}:",
        "if {_bound} doesn't intersect with {_bound2}:"})
@Since("2.3.0")
public class CondBoundIntersects extends Condition {

    static {
        Skript.registerCondition(CondBoundIntersects.class,
                "%bound% (intersects with|overlaps) %location% and %location%",
                "%bound% (intersects with|overlaps) %bound%",
                "%bound% doesn't (intersect with|overlap) %location% and %location%",
                "%bound% doesn't (intersect with|overlap) %bound%");
    }

    private Expression<Bound> bound1, bound2;
    private Expression<Location> loc1, loc2;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.bound1 = (Expression<Bound>) exprs[0];
        if (matchedPattern == 0 || matchedPattern == 2) {
            this.loc1 = (Expression<Location>) exprs[1];
            this.loc2 = (Expression<Location>) exprs[2];
        } else {
            this.bound2 = (Expression<Bound>) exprs[1];
        }
        setNegated(matchedPattern > 1);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        if (this.bound2 != null) {
            return bound1.check(event, bound -> bound2.check(event, bound::overlaps), isNegated());
        } else if (loc1 != null && loc2 != null) {
            Location loc1 = this.loc1.getSingle(event);
            Location loc2 = this.loc2.getSingle(event);
            if (loc1 == null || loc2 == null) return false;
            return bound1.check(event, bound -> bound.overlaps(loc1, loc2), isNegated());
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String bound = this.bound1.toString(e, d);
        String intersect = isNegated() ? " doesn't intersect with " : " intersects with ";
        String possible;
        if (this.bound2 != null) {
            possible = this.bound2.toString(e, d);
        } else {
            String l1 = this.loc1.toString(e, d);
            String l2 = this.loc2.toString(e, d);
            possible = l1 + " and " + l2;
        }
        return bound + intersect + possible;
    }

}
