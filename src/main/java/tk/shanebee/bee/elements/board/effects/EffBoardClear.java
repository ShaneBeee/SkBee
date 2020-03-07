package tk.shanebee.bee.elements.board.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import tk.shanebee.bee.elements.board.objects.Board;

@Name("Board - Clear")
@Description("Clear the scoreboard of a player.  Requires Spigot/Paper 1.13+")
@Examples({"clear scoreboard of player",
        "clear scoreboards of all players"})
@Since("1.0.0")
public class EffBoardClear extends Effect {

    static {
        Skript.registerEffect(EffBoardClear.class,
                "clear %players%'[s] [score]board[s]",
                "clear [score]board[s] of %players%");
    }

    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getArray(event);
        for (Player player : players) {
            Board board = Board.getBoard(player);
            if (board == null) continue;
            board.clearBoard();
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "clear scoreboard of " + this.players.toString(e, d);
    }

}
