package com.shanebeestudios.skbee.elements.fastboard.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.fastboard.FastBoardBase;
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffFastBoardClear extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffFastBoardClear.class,
                "clear [:score|fast]board[s] of %players%",
                "clear %players%'[s] [:score|fast]board[s]")
            .name("FastBoard - Clear")
            .description("Clear a fastboard of a player.",
                "NOTE: You do NOT need to clear a fastboard before changing lines.")
            .examples("clear fastboard of all players",
                "clear fastboard of player")
            .since("1.16.0")
            .register();
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        if (parseResult.hasTag("score")) {
            Skript.warning("'scoreboard' is deprecated, please use 'fastboard' instead.");
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = FastBoardManager.getBoard(player);
            if (board != null) {
                board.clear();
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "clear fastboard[s] of " + this.player.toString(e, d);
    }

}
