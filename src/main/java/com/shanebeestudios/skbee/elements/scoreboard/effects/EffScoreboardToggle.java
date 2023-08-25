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
import com.shanebeestudios.skbee.api.scoreboard.FastBoardWrapper;
import com.shanebeestudios.skbee.api.scoreboard.BoardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Toggle")
@Description("Toggle a scoreboard on or off.")
@Examples({"toggle scoreboards of all players off",
        "toggle player's scoreboard"})
@Since("1.16.0")
public class EffScoreboardToggle extends Effect {

    static {
        Skript.registerEffect(EffScoreboardToggle.class,
                "toggle [score]board[s] of %players% [[to ](1¦(on|true)|2¦(off|false))]",
                "toggle %players%'[s] [score]board[s] [[to ](1¦(on|true)|2¦(off|false))]");
    }

    private Expression<Player> player;
    private int pattern;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        this.pattern = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (Player player : this.player.getArray(event)) {
            FastBoardWrapper board = BoardManager.getBoard(player);
            if (board != null) {
                switch (pattern) {
                    case 0 -> board.toggle();
                    case 1 -> board.show();
                    case 2 -> board.hide();
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String toggle = switch (pattern) {
            case 1 -> " to on";
            case 2 -> " to off";
            default -> "";
        };

        return "toggle scoreboard[s] of " + player.toString(e, d) + toggle;
    }

}
