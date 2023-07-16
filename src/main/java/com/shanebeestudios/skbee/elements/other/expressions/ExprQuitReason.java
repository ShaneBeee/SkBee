package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.jetbrains.annotations.Nullable;

@Name("Quit Reason")
@Description("Represents the different reasons for calling the player quit event (Requires Paper).")
@Examples({"on quit:",
        "\tif quit reason is kicked:",
        "\t\tbroadcast \"%player% is a loser and was kicked haha\""})
@Since("INSERT VERSION")
public class ExprQuitReason extends SimpleExpression<QuitReason> {

    public static final boolean SUPPORTS_QUIT_REASON = Skript.methodExists(PlayerQuitEvent.class, "getReason");

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        if (!SUPPORTS_QUIT_REASON) {
            Skript.error("The 'quit reason' expression can only be used on PaperMC servers.");
            return false;
        } else if (!getParser().isCurrentEvent(PlayerQuitEvent.class)) {
            Skript.error("The 'quit reason' expression can only be used in a player quit event.");
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    protected QuitReason[] get(Event event) {
        if (!(event instanceof PlayerQuitEvent) || !SUPPORTS_QUIT_REASON) return null;
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
