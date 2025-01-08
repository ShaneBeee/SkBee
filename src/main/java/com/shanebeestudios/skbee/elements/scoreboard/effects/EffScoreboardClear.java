package com.shanebeestudios.skbee.elements.scoreboard.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.scoreboard.FastBoardBase;
import com.shanebeestudios.skbee.api.scoreboard.BoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Clear")
@Description({"Clear a scoreboard of a player. NOTE: You do NOT need to clear a scoreboard before changing lines."})
@Examples({"clear scoreboards of all players", "clear scoreboard of player"})
@Since("1.16.0")
public class EffScoreboardClear extends Effect {

    static {
        Skript.registerEffect(EffScoreboardClear.class,
                "clear [score]board[s] of %players%",
                "clear %players%'[s] [score]board[s]");
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?,?> board = BoardManager.getBoard(player);
            if (board != null) {
                board.clear();
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "clear scoreboard[s] of " + player.toString(e, d);
    }

}
