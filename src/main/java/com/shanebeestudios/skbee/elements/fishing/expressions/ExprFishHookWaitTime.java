package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishHookWaitTime extends SimplePropertyExpression<Entity, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFishHookWaitTime.class, Timespan.class,
                "(min|:max) wait time", "entities")
            .name("Fish Hook - Wait time")
            .description("Represents the min/max wait time for a fish hook to catch a fish.",
                "NOTE: this is before applying lure.",
                "NOTE: min wait time must be less than max wait time. Both must be greater than 0.",
                "Defaults: min = 100 ticks (5 seconds), max = 600 ticks (30 seconds).")
            .examples("on fish:",
                "\tif fish state = fishing:",
                "\t\tset min wait time of fish hook to 1 second",
                "\t\tset max wait time of fish hook to 2 seconds")
            .since("2.8.0")
            .register();
    }

    private boolean max;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends Entity>) exprs[0]);
        this.max = parseResult.hasTag("max");
        return true;
    }

    @Override
    public @Nullable Timespan convert(Entity entity) {
        if (entity instanceof FishHook fishHook) {
            int wait = this.max ? fishHook.getMaxWaitTime() : fishHook.getMinWaitTime();
            return new Timespan(Timespan.TimePeriod.TICK, wait);
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, RESET, SET -> CollectionUtils.array(Timespan.class);
            case REMOVE_ALL, DELETE -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue = delta != null && delta[0] != null ? (int) ((Timespan) delta[0]).getAs(Timespan.TimePeriod.TICK) : 0;
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof FishHook fishHook) {
                int value = this.max ? fishHook.getMaxWaitTime() : fishHook.getMinWaitTime();
                switch (mode) {
                    case ADD -> value += changeValue;
                    case SET -> value = changeValue;
                    case RESET -> value = this.max ? 600 : 100;
                    case REMOVE -> value -= changeValue;
                }
                if (value < 0) return;
                if (this.max) {
                    if (value < fishHook.getMinWaitTime()) fishHook.setMinWaitTime(value);
                    fishHook.setMaxWaitTime(value);
                } else {
                    if (value > fishHook.getMaxWaitTime()) fishHook.setMaxWaitTime(value);
                    fishHook.setMinWaitTime(value);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "wait time";
    }

}
