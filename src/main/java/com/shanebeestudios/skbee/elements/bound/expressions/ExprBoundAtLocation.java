package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Location;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - At Location")
@Description("Get a list of bounds/ids at a location.")
@Examples({"set {_bounds::*} to bounds at player", "set {_b::*} to bound ids at player",
        "loop all bounds at player:"})
@Since("1.0.0")
public class ExprBoundAtLocation extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBoundAtLocation.class, Object.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] bound[s] at %location%", "[(all [[of] the]|the)] bound id[s] at %location%");
    }

    private Expression<Location> location;
    private boolean ID;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parse) {
        this.location = (Expression<Location>) exprs[0];
        this.ID = pattern == 1;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event) {
        if (this.location == null) {
            return null;
        }
        Location loc = this.location.getSingle(event);
        if (ID) {
            List<String> ids = new ArrayList<>();
            for (Bound bound : SkBee.getPlugin().getBoundConfig().getBounds()) {
                if (bound.isInRegion(loc)) {
                    ids.add(bound.getId());
                }
            }
            return ids.toArray(new String[0]);
        } else {
            List<Bound> bounds = new ArrayList<>();
            for (Bound bound : SkBee.getPlugin().getBoundConfig().getBounds()) {
                if (bound.isInRegion(loc)) {
                    bounds.add(bound);
                }
            }
            return bounds.toArray(new Bound[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (ID)
            return String.class;
        else
            return Bound.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "bound" + (ID ? " ids" : "s" + " at location " + this.location.toString(e, d));
    }

}
