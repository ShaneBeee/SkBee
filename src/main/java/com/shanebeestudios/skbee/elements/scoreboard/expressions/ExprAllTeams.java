package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

@Name("Team - All Teams")
@Description({
    "Get a list of all teams.",
    "You have the option to get all teams from a specific scoreboard (defaults to the main scoreboard).",
    "Teams off the main scoreboard cannot be serialized/saved to variables (This is due to custom scoreboards not being persistent)."
})
@Examples({
    "set {_teams::*} to all teams",
    "set {_foodBoard} to a custom scoreboard",
    "set {_food::*} to all teams from {_foodBoard}"
})
@Since("1.16.0")
public class ExprAllTeams extends SimpleExpression<Team> {

    static {
        Skript.registerExpression(ExprAllTeams.class, Team.class, ExpressionType.SIMPLE,
            "all teams [(of|from) %scoreboard%]");
    }

    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.scoreboard = (Expression<Scoreboard>) expressions[0];
        return true;
    }

    @Override
    protected Team @Nullable [] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) return new Team[0];
        return scoreboard.getTeams().toArray(Team[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Team> getReturnType() {
        return Team.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug)
            .append("all teams");
        if (!this.scoreboard.isDefault())
            builder.append("from scoreboard", this.scoreboard.toString(event, debug));
        return builder.toString();
    }

}

