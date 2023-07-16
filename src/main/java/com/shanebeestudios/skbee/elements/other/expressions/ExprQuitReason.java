package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.jetbrains.annotations.Nullable;

public class ExprQuitReason extends SimpleExpression<QuitReason> {

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        if (!Skript.methodExists(PlayerQuitEvent.class, "getReason")) {
            Skript.error("The 'quit reason' expression can only be used on PaperMC servers.");
            return false;
        }
        if (!getParser().isCurrentEvent(PlayerQuitEvent.class)) {
            Skript.error("The 'quit reason' expression can only be used in a player quit event.");
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    protected QuitReason[] get(Event event) {
        if (!(event instanceof PlayerQuitEvent)) return null;
        QuitReason reason = ((PlayerQuitEvent) event).getReason();
        return new QuitReason[]{reason};
    }

    @Override
    public Class<? extends QuitReason> getReturnType() {
        return QuitReason.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "quit reason";
    }

}
