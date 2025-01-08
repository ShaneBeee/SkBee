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
import com.shanebeestudios.skbee.api.scoreboard.BoardManager;
import com.shanebeestudios.skbee.api.scoreboard.FastBoardBase;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Line")
@Description({"Get/set/delete lines of a player's scoreboard.",
    "Lines are valid from 1 to 15. 1 being the line at the top and 15 being the bottom (This can be changed in the config).",
    "Line length is unlimited.",
    "Supports number format on Minecraft 1.20.4+ by providing 2 strings (see examples)."})
@Examples({"set line 1 of player's scoreboard to \"Look mah I'm on Minecraft\"",
    "set line 15 of all players' scoreboards to \"I CAN SEE YOU\"",
    "set {_line} to line 10 of player's scoreboard",
    "",
    "#NumberFormat on Minecraft 1.20.4+",
    "set line 1 of scoreboard of player to \"Player:\" and \"&b%name of player%\""})
@Since("1.16.0")
public class ExprScoreboardLine extends SimpleExpression<Object> {

    private static final Class<?>[] CHANGE_TYPES = BoardManager.HAS_ADVENTURE ?
        new Class<?>[]{ComponentWrapper.class, String.class} : new Class<?>[]{String.class};

    static {
        Skript.registerExpression(ExprScoreboardLine.class, Object.class, ExpressionType.COMBINED,
            "line %number% of %players%'[s] [score]board[s]",
            "line %number% of [score]board[s] of %players%");
    }

    private Expression<Number> line;
    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.line = (Expression<Number>) exprs[0];
        this.player = (Expression<Player>) exprs[1];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        Number lineNumber = this.line.getSingle(event);
        if (lineNumber == null) return null;
        int line = lineNumber.intValue();
        if (line < 1 || line > 15) return null;

        List<Object> lines = new ArrayList<>();
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = BoardManager.getBoard(player);
            if (board != null) {
                Object boardLine = board.getLine(line);
                if (boardLine != null) lines.add(boardLine);
            }
        }
        return lines.toArray();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CHANGE_TYPES;
            default -> null;
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object lineValue = delta != null ? delta[0] : null;
        Object lineFormat = delta != null && delta.length > 1 ? delta[1] : null;

        Number lineSingle = this.line.getSingle(event);
        if (lineSingle == null) return;

        int line = lineSingle.intValue();
        if (line < 1 || line > 15) return;

        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = BoardManager.getBoard(player);
            if (board != null) {
                if (mode == ChangeMode.SET) {
                    board.setLine(line, lineValue, lineFormat);
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
    public @NotNull Class<?> getReturnType() {
        return BoardManager.HAS_ADVENTURE ? ComponentWrapper.class : String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "line " + this.line.toString(e, d) + " of scoreboard[s] of " + this.player.toString(e, d);
    }

}
