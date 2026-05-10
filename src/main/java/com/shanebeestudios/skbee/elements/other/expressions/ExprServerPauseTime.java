package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprServerPauseTime extends SimpleExpression<Timespan> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprServerPauseTime.class, Timespan.class,
                "server pause when empty time")
            .name("Server Pause When Empty Time")
            .description("Represents the pause when empty threshold seconds.",
                "To save resources, the server will pause most functions after this time if there are no players online.",
                "Delete will disable this setting.",
                "If the returned value is null that means the setting is disabled.")
            .examples("set {_time} to server pause when empty time",
                "set server pause when empty time to 1 minute",
                "delete server pause when empty time")
            .since("3.23.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected Timespan @Nullable [] get(Event event) {
        int pauseWhenEmptyTime = Bukkit.getPauseWhenEmptyTime();
        if (pauseWhenEmptyTime < 1) {
            // Skript doesn't allow less than 0 Timespans
            // Anything less than 1 is disabled
            return null;
        }
        Timespan timespan = new Timespan(Timespan.TimePeriod.SECOND, pauseWhenEmptyTime);
        return new Timespan[]{timespan};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Timespan.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        int time = -1;
        if (delta != null && delta[0] instanceof Timespan timespan) {
            time = (int) timespan.getAs(Timespan.TimePeriod.SECOND);
        }
        Bukkit.setPauseWhenEmptyTime(time);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "server pause when empty time";
    }

}
