package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Team - State")
@Description("Represents the friendly fire and can see friendly invisibles states of a team.")
@Examples({"set allow friendly fire team state of team named \"a-team\" to true",
        "set can see friendly invisbles team state of team of player to false"})
@Since("1.16.0")
public class ExprTeamState extends SimplePropertyExpression<Team, Boolean> {

    static {
        register(ExprTeamState.class, Boolean.class,
                "(0¦allow friendly fire|1¦can see friendly invisibles) team state", "teams");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Boolean convert(Team team) {
        return pattern == 0 ? team.allowFriendlyFire() : team.canSeeFriendlyInvisibles();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta[0] == null) return;
        boolean state = (boolean) delta[0];
        for (Team team : getExpr().getArray(event)) {
            if (pattern == 0) {
                team.setAllowFriendlyFire(state);
            } else {
                team.setCanSeeFriendlyInvisibles(state);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String state = pattern == 0 ? "allow friendly fire" : "can see friendly invisibles";
        return state + " team state";
    }

}
