package com.shanebeestudios.skbee.elements.scoreboard.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.scoreboard.TeamUtils;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondTeamRegistered extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondTeamRegistered.class,
                "team[s] [named|with id] %strings% [(of|for) %scoreboard%] (is|are) registered",
                "team[s] [named] %strings% [(of|for) %scoreboard%] (isn't|is not|are not|aren't) registered")
            .name("Team - Is Registered")
            .description("Check if a team is registered.",
                "Optionally check for a specific scoreboard (will default to main scoreboard).")
            .examples("if team \"a-team\" is registered:",
                "if team \"b-team\" of player's scoreboard is registered:")
            .since("2.17.0")
            .register();
    }

    private Expression<String> teams;
    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(matchedPattern == 1);
        this.teams = (Expression<String>) exprs[0];
        this.scoreboard = (Expression<Scoreboard>) exprs[1];
        return true;
    }

    @Override
    public boolean check(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) {
            return false;
        }

        return this.teams.check(event, name -> TeamUtils.isRegistered(name, scoreboard), isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String plural = this.teams.isSingle() ? " is" : " are";
        String neg = isNegated() ? " not" : "";
        return "team " + this.teams.toString(e, d) + plural + neg + " registered";
    }

}
