package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.team.type.TeamManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
@Name("Team - Get")
@Description({"Get an instance of a team, either from an entity or by name of team, or get a list of all teams.",
        "If getting a team by name, and it does not exist, a new team with that name will be registered."})
@Examples({"set {_team} to team of player",
        "set {_team} to team named \"le-team\"",
        "set {_teams::*} to all teams"})
@Since("1.16.0")
public class ExprTeam extends SimpleExpression<Team> {

    static {
        Skript.registerExpression(ExprTeam.class, Team.class, ExpressionType.SIMPLE,
                "team named %string%",
                "team of %entity%",
                "all teams");
    }

    private int pattern;
    private Expression<String> name;
    private Expression<Entity> entity;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.name = pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.entity = pattern == 1 ? (Expression<Entity>) exprs[0] : null;
        return true;
    }

    @Override
    protected @Nullable Team[] get(Event event) {
        switch (pattern) {
            case 0 -> {
                String name = this.name.getSingle(event);
                if (name != null) {
                    return new Team[]{TeamManager.getTeam(name)};
                }
            }
            case 1 -> {
                Entity entity = this.entity.getSingle(event);
                if (entity != null) {
                    return new Team[]{TeamManager.getTeam(entity)};
                }
            }
            case 2 -> {
                return TeamManager.getTeams().toArray(new Team[0]);
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return pattern != 2;
    }

    @Override
    public @NotNull Class<? extends Team> getReturnType() {
        return Team.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return switch (this.pattern) {
            case 0 -> "team named " + this.name.toString(e,d);
            case 1 -> "team of " + this.entity.toString(e,d);
            case 2 -> "all teams";
            default -> throw new IllegalStateException("Unexpected value: " + this.pattern);
        };
    }

}
