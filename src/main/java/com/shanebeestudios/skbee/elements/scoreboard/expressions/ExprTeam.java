package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Get")
@Description({"Get an instance of a team, either from an entity or by name of team, or get a list of all teams.",
    "If getting a team by id, and it does not exist, a new team with that id will be registered.",
    "You have the option to get a team from a specific scoreboard (defaults to the main scoreboard).",
    "Teams off the main scoreboard cannot be serialized/saved to variables (This is due to custom scoreboards not being persistent)."})
@Examples({"set {_team} to team of player",
    "set {_team} to team with id \"le-team\"",
    "set {_teams::*} to all teams"})
@Since("1.16.0")
public class ExprTeam extends SimpleExpression<Team> {

    static {
        Skript.registerExpression(ExprTeam.class, Team.class, ExpressionType.SIMPLE,
            "team (named|with id) %string% [(of|from) %scoreboard%]",
            "team of %entity% [(of|from) %scoreboard%]",
            "all teams [(of|from) %scoreboard%]");
    }

    private int pattern;
    private Expression<String> name;
    private Expression<Entity> entity;
    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.name = pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.entity = pattern == 1 ? (Expression<Entity>) exprs[0] : null;
        this.scoreboard = (Expression<Scoreboard>) exprs[matchedPattern == 2 ? 0 : 1];
        return true;
    }

    @Override
    protected Team @Nullable [] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) {
            error("Scoreboard is not set: " + this.scoreboard.toString(event, true));
            return null;
        }
        switch (pattern) {
            case 0 -> {
                String name = this.name.getSingle(event);
                if (name != null) {
                    return new Team[]{TeamUtils.getTeam(name, scoreboard)};
                }
            }
            case 1 -> {
                Entity entity = this.entity.getSingle(event);
                if (entity != null) {
                    return new Team[]{TeamUtils.getTeam(entity, scoreboard)};
                }
            }
            case 2 -> {
                return TeamUtils.getTeams(scoreboard).toArray(new Team[0]);
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
    public @NotNull String toString(Event e, boolean d) {
        String scorebard = this.scoreboard != null ? " from scoreboard " + this.scoreboard.toString(e, true) : "";
        return switch (this.pattern) {
            case 0 -> "team with id " + this.name.toString(e, d) + scorebard;
            case 1 -> "team of " + this.entity.toString(e, d) + scorebard;
            case 2 -> "all teams";
            default -> throw new IllegalStateException("Unexpected value: " + this.pattern);
        };
    }

}
