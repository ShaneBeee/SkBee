package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprAverageTickTime extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprAverageTickTime.class, Number.class,
                "average tick time")
            .name("Average Tick Time - MSPT")
            .description("Represents the average amount of time (in milliseconds) it takes for the server to finish a tick, also know as MSPT")
            .examples("set {_avg} to average tick time",
                "if average tick time > 40:")
            .since("3.5.4")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Number[] get(Event event) {
        return new Number[]{Bukkit.getAverageTickTime()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "average tick time";
    }

}
