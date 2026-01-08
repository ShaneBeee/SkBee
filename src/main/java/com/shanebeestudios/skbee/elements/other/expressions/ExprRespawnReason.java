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
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Respawn Reason")
@Description("Represents the reason the respawn event was called. Requires MC 1.19.4+")
@Examples({"on respawn:",
        "\tif respawn reason = death respawn:",
        "\t\tgive player 10 diamonds"})
@Since("2.8.4")
public class ExprRespawnReason extends SimpleExpression<RespawnReason> {

    static {
        if (!Util.IS_RUNNING_SKRIPT_2_14) {
            Skript.registerExpression(ExprRespawnReason.class, RespawnReason.class, ExpressionType.SIMPLE,
                    "respawn reason");
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(PlayerRespawnEvent.class)) {
            Skript.error("'respawn reason' can only be used in a respawn event.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable RespawnReason[] get(Event event) {
        if (event instanceof PlayerRespawnEvent playerRespawnEvent) {
            return new RespawnReason[]{playerRespawnEvent.getRespawnReason()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends RespawnReason> getReturnType() {
        return RespawnReason.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "respawn reason";
    }

}
