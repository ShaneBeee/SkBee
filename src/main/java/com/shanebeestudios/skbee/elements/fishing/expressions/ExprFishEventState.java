package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishEventState extends SimpleExpression<State> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFishEventState.class, State.class,
                "fish[ing] [event] state")
            .name("Fish Event State")
            .description("Represents the state of a fishing event.")
            .examples("on fish:",
                "\tif fish event state = caught fish:",
                "\t\tcancel event")
            .since("1.15.2")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(PlayerFishEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in a fishing event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable State[] get(Event event) {
        if (!(event instanceof PlayerFishEvent playerFishEvent)) {
            return null;
        }
        return new State[]{playerFishEvent.getState()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends State> getReturnType() {
        return State.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "fish event state";
    }

}
