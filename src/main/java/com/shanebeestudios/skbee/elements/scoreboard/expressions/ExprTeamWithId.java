package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

public class ExprTeamWithId extends SimpleExpression<Team> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprTeamWithId.class, Team.class,
                "team (named|with id) %string% [(of|from) %scoreboard%]")
            .name("Team - From ID")
            .description("Get an instance of a team, by the id. If getting the team and it does not exist, a new team with that name will be registered.",
                "You have the option to get a team from a specific scoreboard (defaults to the main scoreboard).",
                "Teams off the main scoreboard cannot be serialized/saved to variables (This is because custom scoreboards aren't persistent).")
            .examples("set {_newTeam} to team with id \"foo\"",
                "set team of player to team with id \"bar\" from {foodbar}")
            .since("1.16.0")
            .register();
    }

    private Expression<String> teamId;
    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.teamId = (Expression<String>) expressions[0];
        this.scoreboard = (Expression<Scoreboard>) expressions[1];
        return true;
    }

    @Override
    protected Team @Nullable [] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        String teamId = this.teamId.getSingle(event);
        if (scoreboard == null || teamId == null) return new Team[0];
        return new Team[]{TeamUtils.getTeam(teamId, scoreboard)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Team> getReturnType() {
        return Team.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        SyntaxStringBuilder syntaxBuilder = new SyntaxStringBuilder(event, debug);
        syntaxBuilder.append("team with id", this.teamId);
        if (!this.scoreboard.isDefault())
            syntaxBuilder.append("from scoreboard", this.scoreboard);
        return syntaxBuilder.toString();
    }

}
