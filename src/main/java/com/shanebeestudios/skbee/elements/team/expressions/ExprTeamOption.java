package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Team - Options")
@Description("Represents an option for a team.")
@Examples("set team option name tag visibility of team named \"a-team\" to never")
@Since("1.16.0")
public class ExprTeamOption extends SimpleExpression<OptionStatus> {

    static {
        Skript.registerExpression(ExprTeamOption.class, OptionStatus.class, ExpressionType.COMBINED,
                "team option %teamoption% of %teams%",
                "%teams%'[s] team option %teamoption%");
    }

    private Expression<Option> teamOption;
    private Expression<Team> teams;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.teamOption = (Expression<Option>) exprs[matchedPattern];
        this.teams = (Expression<Team>) exprs[matchedPattern == 0 ? 1 : 0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected OptionStatus[] get(Event event) {
        Option teamOption = this.teamOption.getSingle(event);

        if (teamOption == null) return null;

        List<OptionStatus> statuses = new ArrayList<>();
        for (Team team : this.teams.getArray(event)) {
            statuses.add(team.getOption(teamOption));
        }
        return statuses.toArray(new OptionStatus[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(OptionStatus.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Option teamOption = this.teamOption.getSingle(event);
        OptionStatus optionStatus = (OptionStatus) delta[0];
        if (teamOption == null || optionStatus == null) return;

        for (Team team : this.teams.getArray(event)) {
            team.setOption(teamOption, optionStatus);
        }
    }

    @Override
    public boolean isSingle() {
        return this.teams.isSingle();
    }

    @Override
    public @NotNull Class<? extends OptionStatus> getReturnType() {
        return OptionStatus.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "team option " + teamOption.toString(e, d) + " of " + this.teams.toString(e, d);
    }

}
