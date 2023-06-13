package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.scoreboard.BoardManager;
import com.shanebeestudios.skbee.api.scoreboard.FastBoardWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
@Name("Scoreboard - Line")
@Description({"Get/set/delete lines of a player's scoreboard. Lines are valid from 1 to 15. 1 being the line at the top",
        "and 15 being the bottom. Line length is unlimited."})
@Examples({"set line 1 of player's scoreboard to \"Look mah I'm on Minecraft\"",
        "set line 15 of all players' scoreboards to \"I CAN SEE YOU\"",
        "set {_line} to line 10 of player's scoreboard"})
@Since("1.16.0")
public class ExprScoreboardLine extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprScoreboardLine.class, String.class, ExpressionType.COMBINED,
                "line %number% of %players%'[s] [score]board[s]",
                "line %number% of [score]board[s] of %players%");
    }

    private Expression<Number> line;
    private Expression<Player> player;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.line = (Expression<Number>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        Number lineNumber = this.line.getSingle(event);
        if (lineNumber == null) return null;
        int line = lineNumber.intValue();
        if (line < 1 || line > 15) return null;

        List<String> lines = new ArrayList<>();
        for (Player player : this.player.getArray(event)) {
            FastBoardWrapper board = BoardManager.getBoard(player);
            if (board != null) {
                lines.add(board.getLine(line));
            }
        }
        return lines.toArray(new String[0]);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(String.class);
            default -> null;
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        String lineString = delta != null ? (String) delta[0] : null;

        Number lineSingle = this.line.getSingle(event);
        if (lineSingle == null) return;

        int line = lineSingle.intValue();
        if (line < 1 || line > 15) return;

        for (Player player : this.player.getArray(event)) {
            FastBoardWrapper board = BoardManager.getBoard(player);
            if (board != null) {
                if (mode == ChangeMode.SET) {
                    board.setLine(line, lineString);
                } else if (mode == ChangeMode.DELETE) {
                    board.deleteLine(line);
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.player.isSingle();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "line " + this.line.toString(e, d) + " of scoreboard[s] of " + this.player.toString(e, d);
    }

}
