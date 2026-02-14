package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class ExprPlayerIdleTime extends SimplePropertyExpression<Player, Timespan> {

    public static void register(Registration reg) {
        if (Skript.methodExists(Player.class, "getIdleDuration")) {
            reg.newPropertyExpression(ExprPlayerIdleTime.class, Timespan.class,
                "idle (time|duration)", "players")
                .name("Player Idle Time")
                .description("Get/reset the idle time of a player.",
                    "The idle duration is reset when the player sends specific action packets.",
                    "Requires Paper 1.20.2+")
                .examples("if idle time of player > 1 minute:",
                    "\tkick player due to \"hanging out too long\"")
                .since("3.0.0")
                .register();
        }
    }

    @Override
    public @Nullable Timespan convert(Player player) {
        Duration idleDuration = player.getIdleDuration();
        return new Timespan(idleDuration.toMillis());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode != ChangeMode.RESET) return;
        for (Player player : getExpr().getArray(event)) {
            player.resetIdleDuration();
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "idle time";
    }

}
