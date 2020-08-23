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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.elements.board.objects.Board;

@Name("Board - Toggle")
@Description("Toggle the scoreboard of a player on or off.")
@Examples({"toggle board of player",
        "if scoreboard of player is on:",
        "\ttoggle scoreboard of player off", "",
        "toggle scoreboards of all players off"})
@Since("1.0.0")
public class EffBoardToggle extends Effect {

    static {
        Skript.registerEffect(EffBoardToggle.class,
                "toggle [score]board[s] of %players% [[to] (1¦(on|true)|2¦(off|false))]",
                "toggle %players%'[s] [score]board[s] [[to] (1¦(on|true)|2¦(off|false))]");
    }

    private Expression<Player> players;
    private int parse;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int i, @NotNull Kleenean kleenean, ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        parse = parseResult.mark;
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        Player[] players = this.players.getArray(event);
        for (Player player : players) {
            Board board = Board.getBoard(player);
            if (board == null) continue;
            switch (parse) {
                case 0:
                    board.toggle(!board.isOn());
                    break;
                case 1:
                    board.toggle(true);
                    break;
                case 2:
                    board.toggle(false);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String toggle = parse == 1 ? " to on" : parse == 2 ? " to off" : "";
        return "toggle scoreboard of " + this.players.toString(e, d) + toggle;
    }

}
