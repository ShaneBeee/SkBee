package com.shanebeestudios.skbee.elements.other.expressions;

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
import org.eclipse.jdt.annotation.Nullable;

@Name("Fish Event State")
@Description("Represents the state of a fishing event.")
@Examples({"on fish:",
        "\tif fish event state = caught fish:",
        "\t\tcancel event"})
@Since("INSERT VERSION")
public class ExprFishEventState extends SimpleExpression<State> {

    static {
        Skript.registerExpression(ExprFishEventState.class, State.class, ExpressionType.SIMPLE,
                "fish[ing] [event] state");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(PlayerFishEvent.class)) {
            Skript.error("Fish event state can only be used in a fishing event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable State[] get(Event event) {
        if (!(event instanceof PlayerFishEvent)) {
            return null;
        }
        return new State[]{((PlayerFishEvent) event).getState()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends State> getReturnType() {
        return State.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "fish event state";
    }

}
