package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Fish Event State")
@Description("Represents the state of a fishing event.")
@Examples({"on fish:",
        "\tif fish event state = caught fish:",
        "\t\tcancel event"})
@Since("1.15.2")
public class ExprFishEventState extends SimpleExpression<State> {

    static {
        Skript.registerExpression(ExprFishEventState.class, State.class, ExpressionType.SIMPLE,
                "fish[ing] [event] state");
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
