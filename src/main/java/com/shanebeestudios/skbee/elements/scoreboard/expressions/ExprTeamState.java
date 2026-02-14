package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTeamState extends SimplePropertyExpression<Team, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprTeamState.class, Boolean.class,
                "(allow friendly fire|1:can see friendly invisibles) team state", "teams")
            .name("Team - State")
            .description("Represents the friendly fire and can see friendly invisibles states of a team.")
            .examples("set allow friendly fire team state of team named \"a-team\" to true",
                "set can see friendly invisbles team state of team of player to false")
            .since("1.16.0")
            .register();
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
