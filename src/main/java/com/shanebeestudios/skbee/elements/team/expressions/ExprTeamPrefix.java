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
import com.shanebeestudios.skbee.api.reflection.ChatReflection;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
@Name("Team - Prefix/Suffix")
@Description("Get/set/delete the prefix/suffix of a team.")
@Examples({"set team prefix of {_team} to \"[OWNER]\"",
        "set team suffix of {_team} to \"[GOLD]\"",
        "set {_pre} to team prefix of {_team}",
        "set team prefix of team of player to \"[BestTeam]\""})
@Since("1.16.0")
public class ExprTeamPrefix extends SimplePropertyExpression<Team, String> {

    static {
        register(ExprTeamPrefix.class, String.class,
                "team (0¦prefix|1¦suffix)", "team");
    }

    private int pattern;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<? extends Team>) exprs[0]);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable String convert(Team team) {
        if (pattern == 0) {
            return team.getPrefix();
        } else {
            return team.getSuffix();
        }
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> CollectionUtils.array(String.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Team[] teams = getExpr().getArray(event);
        String name = mode == ChangeMode.SET ? (String) delta[0] : null;
        for (Team team : teams) {
            if (pattern == 0) {
                ChatReflection.setTeamPrefix(team, name);
            } else {
                ChatReflection.setTeamSuffix(team, name);
            }
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "team " + (pattern == 0 ? "prefix" : "suffix");
    }

}
