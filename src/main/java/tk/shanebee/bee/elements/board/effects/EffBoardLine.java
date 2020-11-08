package tk.shanebee.bee.elements.board.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.elements.board.objects.Board;

@Name("Board - Line")
@Description("set/delete a line in a player's scoreboard. Note: line 1 is bottom, line 15 is top. " +
        "Accepts texts and text components (text components as of 1.5.1).")
@Examples({"set line 1 of player's scoreboard to \"oooo I'm a line!!\"",
        "set line 15 of all players' scoreboards to \"I'm the top line!!!\"",
        "delete line 3 of player's scoreboard",
        "delete line 4 of all players' scoreboards", "",
        "set {_t} to translate component from player's tool",
        "set line 1 of player's scoreboard to {_t}"})
@Since("1.0.0")
public class EffBoardLine extends Effect {

    static {
        Skript.registerEffect(EffBoardLine.class,
                "set line %number% of %players%'[s] [score]board[s] to %basecomponent/string%",
                "set line %number% of [score]board[s] of %players% to %basecomponent/string%",
                "delete line %number% of %players%'[s] [score]board[s]",
                "delete line %number% of [score]board[s] of %players%");
    }

    private Expression<Number> line;
    private Expression<Player> players;
    private Expression<Object> text;
    private boolean set;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        line = (Expression<Number>) exprs[0];
        players = (Expression<Player>) exprs[1];
        set = matchedPattern <= 1;
        if (set) {
            text = (Expression<Object>) exprs[2];
        }
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        Player[] players = this.players.getArray(event);
        int line = this.line.getSingle(event).intValue();
        if (line > 15 || line < 1) return; // Only set lines 1-15

        String text = "";
        if (set) {
            Object object = this.text.getSingle(event);
            if (object instanceof BaseComponent) {
                text = ((BaseComponent) object).toLegacyText();
            } else {
                text = (String) object;
            }
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
    public @NotNull String toString(Event e, boolean d) {
        String set = this.set ? "set" : "delete";
        String string = this.set ? " to " + text.toString(e, d) : "";
        return set + " line " + line.toString(e, d) + " of " + players.toString(e,d) + " scoreboard" + string;
    }

}
