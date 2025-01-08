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
import com.shanebeestudios.skbee.api.scoreboard.FastBoardBase;
import com.shanebeestudios.skbee.api.scoreboard.FastBoardLegacy;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Title")
@Description("Get/set the title of a scoreboard.")
@Examples({"set title of player's scoreboard to \"Le Title\"",
    "set {_title} to title of scoreboard of player"})
@Since("1.16.0")
public class ExprScoreboardTitle extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprScoreboardTitle.class, Object.class, ExpressionType.PROPERTY,
            "title of %players%'[s] [score]board[s]",
            "title of [score]board of %players%");
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        List<Object> titles = new ArrayList<>();
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = BoardManager.getBoard(player);
            if (board == null) continue;
            titles.add(board.getTitle());
        }
        return titles.toArray();
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(String.class, ComponentWrapper.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object title = delta != null ? delta[0] : null;
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?,?> board = BoardManager.getBoard(player);
            if (board != null) {
                board.setTitle(title);
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
        return "title of scoreboard of " + this.player.toString(e, d);
    }

}
