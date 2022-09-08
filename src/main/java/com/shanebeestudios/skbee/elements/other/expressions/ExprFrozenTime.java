package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Entity Frozen Time")
@Description("Get/change the frozen time (as a TimeSpan) of an entity. (Added in Minecraft 1.17)")
@Examples({"set frozen time of player to 10 minutes",
        "add 5 seconds to frozen time of target entity",
        "remove 10 ticks from frozen time of player",
        "reset frozen time of all entities",
        "set {_ticks} to frozen time of player"})
@Since("2.3.0")
public class ExprFrozenTime extends SimplePropertyExpression<Entity, Timespan> {

    static {
        register(ExprFrozenTime.class, Timespan.class, "frozen time", "entities");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Entity.class, "getFreezeTicks")) {
            Skript.error("Frozen time is not available on your server version!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Timespan convert(Entity entity) {
        return Timespan.fromTicks_i(entity.getFreezeTicks());
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
            entity.setFreezeTicks(0);
            return;
        }

        if (delta != null && delta[0] instanceof Timespan timespan) {
            long timespanTicks = timespan.getTicks_i();
            long freezeTicks = entity.getFreezeTicks();

            switch (mode) {
                case ADD -> freezeTicks += timespanTicks;
                case DELETE -> freezeTicks -= timespanTicks;
                case SET -> freezeTicks = timespanTicks;
            }
            entity.setFreezeTicks((int) freezeTicks);
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "frozen time";
    }

}
