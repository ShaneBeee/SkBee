package com.shanebeestudios.skbee.elements.fastboard.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.fastboard.FastBoardBase;
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("FastBoard - Clear")
@Description({"Clear a fastboard of a player.",
    "NOTE: You do NOT need to clear a fastboard before changing lines."})
@Examples({"clear fastboard of all players",
    "clear fastboard of player"})
@Since("1.16.0")
public class EffFastBoardClear extends Effect {

    static {
        Skript.registerEffect(EffFastBoardClear.class,
                "clear [:score|fast]board[s] of %players%",
                "clear %players%'[s] [:score|fast]board[s]");
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
            FastBoardBase<?,?> board = FastBoardManager.getBoard(player);
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
