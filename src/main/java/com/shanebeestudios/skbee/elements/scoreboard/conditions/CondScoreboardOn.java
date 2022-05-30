package com.shanebeestudios.skbee.elements.scoreboard.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.board.conditions.CondBoardOn;
import com.shanebeestudios.skbee.elements.scoreboard.objects.Board;
import com.shanebeestudios.skbee.elements.scoreboard.objects.BoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Is On")
@Description("Check if a player's scoreboard is currently toggled on/off.")
@Examples({"if scoreboard of player is on:",
        "\tsend \"Your scoreboard is on!\""})
@Since("1.16.0")
public class CondScoreboardOn extends Condition {

    static {
        Skript.registerCondition(CondBoardOn.class,
                "[score]board of %player% is (on|true)",
                "[score]board of %player% is(n't| not) on",
                "[score]board of %player% is (off|false)");
    }

    private Expression<Player> player;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        setNegated(matchedPattern >= 1);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        Player player = this.player.getSingle(event);
        if (player != null) {
            Board board = BoardManager.getBoard(player);
            return board.isOn() ^ isNegated();
        }

        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "scoreboard of " + this.player.toString(e, d) + " is " + (isNegated() ? "off" : "on");
    }

}
