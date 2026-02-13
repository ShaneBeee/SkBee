package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRespawnReason extends SimpleExpression<RespawnReason> {

    public static void register(Registration reg) {
        if (!Util.IS_RUNNING_SKRIPT_2_14) {
            reg.newSimpleExpression(ExprRespawnReason.class, RespawnReason.class,
                    "respawn reason")
                .name("Respawn Reason")
                .description("Represents the reason the respawn event was called. Requires MC 1.19.4+")
                .examples("on respawn:",
                    "\tif respawn reason = death respawn:",
                    "\t\tgive player 10 diamonds")
                .since("2.8.4")
                .register();
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
