package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Display Name")
@Description("Represents the display name of a team.")
@Examples("set team display name of {_team} to \"The Warriors\"")
@Since("2.8.4")
@SuppressWarnings("deprecation")
public class ExprTeamName extends SimplePropertyExpression<Team, String> {

    static {
        register(ExprTeamName.class, String.class, "team [display] name", "teams");
    }

    @Override
    public @Nullable String convert(Team team) {
        return team.getDisplayName();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof String name) {
            for (Team team : getExpr().getArray(event)) {
                team.setDisplayName(name);
            }
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "team display name";
    }

}
