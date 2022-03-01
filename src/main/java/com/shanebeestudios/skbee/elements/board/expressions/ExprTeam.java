package com.shanebeestudios.skbee.elements.board.expressions;

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
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeam;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeams;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Get Team")
@Description("Get the team by name or of an entity, or get a list of all teams.")
@Examples({"set {_t} to team named \"a-team\"",
        "set {_t} to team of player",
        "set {_teams::*} to all bee teams"})
@Since("1.15.0")
public class ExprTeam extends SimpleExpression<BeeTeam> {

    private static final BeeTeams BEE_TEAMS;

    static {
        BEE_TEAMS = SkBee.getPlugin().getBeeTeams();
        Skript.registerExpression(ExprTeam.class, BeeTeam.class, ExpressionType.SIMPLE,
                "[[sk]bee[ ]]team named %string%",
                "[[sk]bee[ ]]team of %entity%",
                "all [[sk]bee[ ]]teams");
    }

    private int pattern;
    private Expression<String> name;
    private Expression<Entity> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.name = pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.entity = pattern == 1 ? (Expression<Entity>) exprs[0] : null;
        return true;
    }

    @Nullable
    @Override
    protected BeeTeam[] get(@NotNull Event event) {
        if (pattern == 0) {
            String name = this.name.getSingle(event);
            if (name == null) return null;

            return new BeeTeam[]{BEE_TEAMS.getBeeTeam(name)};
        } else if (pattern == 1) {
            Entity entity = this.entity.getSingle(event);
            if (entity == null) return null;

            return new BeeTeam[]{BEE_TEAMS.getBeeTeamByEntry(entity)};
        } else if (pattern == 2) {
            return BEE_TEAMS.getTeams().toArray(new BeeTeam[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return pattern != 2;
    }

    @Override
    public @NotNull Class<? extends BeeTeam> getReturnType() {
        return BeeTeam.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 2) {
            return "all SkBee teams";
        }
        String from = pattern == 0 ? "named " + this.name.toString(e, d) : "of entity " + this.entity.toString(e, d);
        return "team " + from;
    }

}
