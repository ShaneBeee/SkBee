package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprBoundFromID extends SimpleExpression<Bound> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBoundFromID.class, Bound.class,
                "bound[s] (of|from|with) id[s] %strings%")
            .name("Bound - From ID")
            .description("Get a bound object from a bound ID")
            .examples("set {_b} to bound from id \"%player%.home\"")
            .since("1.0.0")
            .register();
    }

    private Expression<String> ids;
    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.ids = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Bound[] get(Event event) {
        List<Bound> bounds = new ArrayList<>();
        for (String id : this.ids.getAll(event)) {
            Bound bound = boundConfig.getBoundFromID(id);
            if (bounds.contains(bound)) continue;
            bounds.add(bound);
        }
        return bounds.toArray(new Bound[0]);
    }

    @Override
    public boolean getAnd() {
        return this.ids.getAnd();
    }

    @Override
    public boolean isSingle() {
        return ids.isSingle();
    }

    @Override
    public @NotNull Class<? extends Bound> getReturnType() {
        return Bound.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "bound[s] from id " + this.ids.toString(event, debug);
    }

}
