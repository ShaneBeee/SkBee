package com.shanebeestudios.skbee.elements.team.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.team.type.TeamManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Is Registered")
@Description("Check if a team is registered.")
@Examples("if team \"a-team\" is registered:")
@Since("INSERT VEERSION")
public class CondTeamRegistered extends Condition {

    static {
        Skript.registerCondition(CondTeamRegistered.class,
                "team[s] [named] %strings% (is|are) registered",
                "team[s] [named] %strings% (isn't|is not|are not|aren't) registered");
    }

    private Expression<String> teams;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(matchedPattern == 1);
        this.teams = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        return this.teams.check(event, TeamManager::isRegistered, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String plural = this.teams.isSingle() ? " is" : " are";
        String neg = isNegated() ? " not" : "";
        return "team " + this.toString(e, d) + plural + neg + " registered";
    }

}
