package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - From Entity")
@Description({
    "Get an instance of a team from an entity.",
    "You have the option to get a team from a specific scoreboard (defaults to the main scoreboard).",
    "Teams off the main scoreboard cannot be serialized/saved to variables (This is because custom scoreboards aren't persistent)."
})
@Examples({
    "set {_team} to team of player",
    "set {_team} to team of player from {-teams::groups}"
})
@Since("1.16.0")
public class ExprTeam extends SimpleExpression<Team> {

    static {
        Skript.registerExpression(ExprTeam.class, Team.class, ExpressionType.SIMPLE,
            "team of %entity% [(of|from) %scoreboard%]");
    }

    private Expression<Entity> entities;
    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.scoreboard = (Expression<Scoreboard>) exprs[1];
        return true;
    }

    @Override
    protected Team @Nullable [] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) return null;
        return this.entities.stream(event)
            .map(entity -> TeamUtils.getTeam(entity, scoreboard))
            .toArray(Team[]::new);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Team.class);
        }
        return super.acceptChange(mode);
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {

            Team team = delta != null && delta[0] instanceof Team deltaTeam ? deltaTeam : null;
            if (team == null || this.entities == null) return;

            Entity entity = this.entities.getSingle(event);
            if (entity == null) return;

            team.addEntity(entity);
            return;
        }
        // Delegate to default changers
        super.change(event, delta, mode);
    }

    @Override
    public boolean isSingle() {
        return this.entities.isSingle();
    }

    @Override
    public @NotNull Class<? extends Team> getReturnType() {
        return Team.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
        builder.append("team of", this.entities);
        if (!this.scoreboard.isDefault())
            builder.append("from scoreboard", this.scoreboard);
        return builder.toString();
    }

}
