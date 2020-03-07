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
import tk.shanebee.bee.elements.board.objects.Board;

@Name("Board - Line")
@Description("set/delete a line in a player's scoreboard. Note: line 1 is bottom, line 15 is top. Requires Spigot/Paper 1.13+")
@Examples({"set line 1 of player's scoreboard to \"oooo I'm a line!!\"",
        "set line 15 of all player's scoreboards to \"I'm the top line!!!\"",
        "delete line 3 of player's scoreboard",
        "delete line 4 of all player's scoreboards"})
@Since("1.0.0")
public class EffBoardLine extends Effect {

    static {
        Skript.registerEffect(EffBoardLine.class,
                "set line %number% of %players%'[s] [score]board[s] to %string%",
                "delete line %number% of %players%'[s] [score]board[s]");
    }

    private Expression<Number> line;
    private Expression<Player> players;
    private Expression<String> text;
    private boolean set;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        line = (Expression<Number>) exprs[0];
        players = (Expression<Player>) exprs[1];
        set = matchedPattern == 0;
        if (set) {
            text = (Expression<String>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getArray(event);
        int line = this.line.getSingle(event).intValue();
        if (line > 15 || line < 1) return; // Only set lines 1-15

        String text = "";
        if (set) {
            text = this.text.getSingle(event);
        }

        for (Player player : players) {
            Board board = Board.getBoard(player);
            if (board == null) continue;
            if (set)
                board.setLine(line, text);
            else
                board.deleteLine(line);
        }

    }

    @Override
    public String toString(Event e, boolean d) {
        String set = this.set ? "set" : "delete";
        String string = this.set ? " to " + text.toString(e, d) : "";
        return set + " line " + line.toString(e, d) + " of " + players.toString(e,d) + " scoreboard" + string;
    }

}
