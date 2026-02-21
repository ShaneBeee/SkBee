package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprBoundsAtLocation extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBoundsAtLocation.class, Object.class,
                "[all [[of] the]|the] [-1:temporary|1:non[-| ]temporary] bound[s] [:id[s]] at %locations%")
            .name("Bound - Bounds at Location")
            .description("Get a list of non-temporary, temporary, or all bounds bounds/ids at a location.")
            .examples("set {_temporaryBounds::*} to temporary bounds at player",
                "set {_nonTemporaryBounds::*} to nontemporary bounds at {_loc}",
                "loop all bounds at {locations::*}:",
                "\tbroadcast loop-bound")
            .since("1.0.0, 2.15.0 (temporary/non-temporary)")
            .register();
    }

    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();
    private Expression<Location> locations;
    private boolean ID;
    private Kleenean boundType;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parse) {
        this.locations = (Expression<Location>) exprs[0];
        this.ID = parse.hasTag("id");
        this.boundType = Kleenean.get(parse.mark);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event) {
        List<Bound> bounds = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Location location : this.locations.getArray(event)) {
            for (Bound bound : boundConfig.getBoundsAt(location)) {
                if (bounds.contains(bound) || ids.contains(bound.getId())) continue;
                if (boundType == Kleenean.FALSE && !bound.isTemporary()) continue;
                if (boundType == Kleenean.TRUE && bound.isTemporary()) continue;
                if (ID) {
                    ids.add(bound.getId());
                } else {
                    bounds.add(bound);
                }
            }
        }
        return ID ? ids.toArray(new String[0]) : bounds.toArray(new Bound[0]);
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
    public @NotNull String toString(Event event, boolean debug) {
        String ID = this.ID ? " ids" : "s";
        String atLocation = " at " + locations.toString(event, debug);
        return switch (boundType) {
            case TRUE -> "all non-temporary bound";
            case FALSE -> "all temporary bound";
            case UNKNOWN -> "all bound";
        } + ID + atLocation;
    }

}
