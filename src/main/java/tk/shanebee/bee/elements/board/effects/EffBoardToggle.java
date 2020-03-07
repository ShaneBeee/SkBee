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

@Name("Board - Toggle")
@Description("Toggle the scoreboard of a player on or off. Requires Spigot/Paper 1.13+")
@Examples({"if scoreboard of player is on:",
        "\ttoggle scoreboard of player off", "",
        "toggle scoreboards of all players off"})
@Since("1.0.0")
public class EffBoardToggle extends Effect {

    static {
        Skript.registerEffect(EffBoardToggle.class,
                "toggle [score]board[s] of %players% [to] (0¦on|1¦off)",
                "toggle %players%'[s] [score]board[s] [to] (0¦on|1¦off)");
    }

    private Expression<Player> players;
    private boolean on;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        on = parseResult.mark != 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getArray(event);
        for (Player player : players) {
            Board board = Board.getBoard(player);
            if (board == null) continue;
            board.toggle(this.on);
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "toggle scoreboard of " + this.players.toString(e, d) + "to " + (this.on ? "on" : "off");
    }

}
