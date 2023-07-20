package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Entity Fire Time")
@Description("Get/change the fire time (as a TimeSpan) of an entity.")
@Examples({"set fire time of player to 10 minutes",
        "add 5 seconds to fire time of target entity",
        "remove 10 ticks from fire time of player",
        "reset fire time of all entities",
        "set {_ticks} to fire time of player"})
@Since("2.3.0")
public class ExprFireTime extends SimplePropertyExpression<Entity, Timespan> {

    static {
        register(ExprFireTime.class, Timespan.class, "fire time", "entities");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        Util.skript27Warning("entity fire burn duration", "expression");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Timespan convert(Entity entity) {
        return Timespan.fromTicks_i(Math.max(entity.getFireTicks(), 0));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, DELETE, RESET, SET -> CollectionUtils.array(Timespan.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Entity entity = getExpr().getSingle(event);
        if (entity == null) return;

        if (mode == ChangeMode.RESET) {
            entity.setFireTicks(0);
            return;
        }

        if (delta != null && delta[0] instanceof Timespan timespan) {
            long timespanTicks = timespan.getTicks_i();
            long fireTicks = entity.getFireTicks();

            switch (mode) {
                case ADD -> fireTicks += timespanTicks;
                case DELETE -> fireTicks -= timespanTicks;
                case SET -> fireTicks = timespanTicks;
            }
            entity.setFireTicks((int) fireTicks);
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "fire time";
    }

}
