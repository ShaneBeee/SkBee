package tk.shanebee.bee.elements.board.expression;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.elements.board.objects.BeeTeam;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
@Name("Team - Options")
@Description("Represents an option for a team.")
@Examples("set team option name tag visibility of team \"a-team\" to never")
@Since("INSERT VERSION")
public class ExprTeamOption extends SimpleExpression<Team.OptionStatus> {

    static {
        Skript.registerExpression(ExprTeamOption.class, Team.OptionStatus.class, ExpressionType.COMBINED,
                "team option %teamoption% of %beeteams%",
                "%beeteams%'[s] team option %teamoption%");
    }

    private Expression<Team.Option> teamOption;
    private Expression<BeeTeam> team;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        this.teamOption = (Expression<Team.Option>) exprs[matchedPattern];
        this.team = (Expression<BeeTeam>) exprs[matchedPattern == 0 ? 1 : 0];
        return true;
    }

    @Nullable
    @Override
    protected Team.OptionStatus[] get(@NotNull Event event) {
        BeeTeam[] teams = this.team.getArray(event);
        Team.Option teamOption = this.teamOption.getSingle(event);
        if (teamOption == null) return null;

        List<Team.OptionStatus> statuses = new ArrayList<>();
        for (BeeTeam team : teams) {
            statuses.add(team.getTeamOption(teamOption));
        }
        return statuses.toArray(new Team.OptionStatus[0]);
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Team.OptionStatus.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        BeeTeam[] teams = this.team.getArray(event);
        Team.Option teamOption = this.teamOption.getSingle(event);
        Team.OptionStatus teamOptionStatus = ((Team.OptionStatus) delta[0]);
        if (teamOption == null || teamOptionStatus == null) return;

        for (BeeTeam team : teams) {
            team.setTeamOption(teamOption, teamOptionStatus);
        }
    }

    @Override
    public boolean isSingle() {
        return this.team.isSingle();
    }

    @Override
    public @NotNull Class<? extends Team.OptionStatus> getReturnType() {
        return Team.OptionStatus.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("team option '%s' of team[s] '%s'",
                teamOption.toString(e, d),
                team.toString(e, d));
    }

}
