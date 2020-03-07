package tk.shanebee.bee.elements.board.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import tk.shanebee.bee.elements.board.objects.Board;

@Name("Board - Is on")
@Description("Check if a player's scoreboard is currently toggled on or off. Requires Spigot/Paper 1.13+")
@Examples({"if scoreboard of player is on:",
        "\ttoggle scoreboard of player off",
        "if scoreboard of player is off:",
        "\ttoggle scoreboard of player on"})
@Since("1.0.0")
public class CondBoardOn extends Condition {

    static {
        Skript.registerCondition(CondBoardOn.class,
                "[score]board of %player% is (on|true)",
                "[score]board of %player% is(n't| not) on",
                "[score]board of %player% is (off|false)");
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        setNegated(i >= 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Player player = this.player.getSingle(event);
        if (player != null) {
            Board board = Board.getBoard(player);
            return board.isOn() ^ isNegated();
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "scoreboard of " + this.player.toString(e, d) + " is " + (isNegated() ? "off" : "on");
    }

}
