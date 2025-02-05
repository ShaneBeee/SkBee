package com.shanebeestudios.skbee.elements.fastboard.expressions;

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
import com.shanebeestudios.skbee.api.fastboard.FastBoardManager;
import com.shanebeestudios.skbee.api.fastboard.FastBoardBase;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("FastBoard - Title")
@Description({"Get/set the title of a fastboard.",
    "When running Paper, text components are supported."})
@Examples({"set title of player's fastboard to \"Le Title\"",
    "set {_title} to title of fastboard of player",
    "",
    "# Component Support",
    "set title of player's fastboard to mini message from \"<rainbow>Le Title\"",
    "set title of player's fastboard to mini message from \"<font:uniform>Le Title\""})
@Since("1.16.0")
public class ExprFastBoardTitle extends SimpleExpression<Object> {

    private static final Class<?>[] CHANGE_TYPES = FastBoardManager.HAS_ADVENTURE ?
        new Class<?>[]{ComponentWrapper.class, String.class} : new Class<?>[]{String.class};

    static {
        Skript.registerExpression(ExprFastBoardTitle.class, Object.class, ExpressionType.PROPERTY,
            "title of %players%'[s] [:score|fast]board[s]",
            "title of [:score|fast]board of %players%");
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        if (parseResult.hasTag("score")) {
            Skript.warning("'scoreboard' is deprecated, please use 'fastboard' instead.");
        }
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        List<Object> titles = new ArrayList<>();
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = FastBoardManager.getBoard(player);
            if (board == null) continue;
            titles.add(board.getTitle());
        }
        return titles.toArray();
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CHANGE_TYPES;
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object title = delta != null ? delta[0] : null;
        for (Player player : this.player.getArray(event)) {
            FastBoardBase<?, ?> board = FastBoardManager.getBoard(player);
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
        return FastBoardManager.HAS_ADVENTURE ? ComponentWrapper.class : String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "title of fastboard of " + this.player.toString(e, d);
    }

}
