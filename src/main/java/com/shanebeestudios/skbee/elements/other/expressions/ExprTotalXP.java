package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Total Experience")
@Description("Represents the total experience points the player currently has.")
@Examples({"add 10 to total experience of player", "remove 100 from total experience of player",
        "set total xp of player to 1500", "set {_t} to total experience of player"})
@Since("1.2.0")
public class ExprTotalXP extends PropertyExpression<Player, Integer> {

    static {
        register(ExprTotalXP.class, Integer.class, "total (xp|experience) [points]", "players");
    }

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<Player>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Integer[] get(Event e, Player[] source) {
        return get(source, new Getter<Integer, Player>() {
            @Nullable
            @Override
            public Integer get(Player player) {
                return PlayerUtils.getTotalXP(player);
            }
        });
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {
        int change = delta == null ? 0 : ((Number) delta[0]).intValue();
        for (Player player : getExpr().getArray(e)) {
            int value = PlayerUtils.getTotalXP(player);
            switch (mode) {
                case ADD -> value += change;
                case REMOVE -> value -= change;
                case SET -> value = change;
            }
            PlayerUtils.setTotalXP(player, value);
        }
    }

    @Override
    public @NotNull Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "total experience points of " + getExpr().toString(e, d);
    }

}
