package com.shanebeestudios.skbee.elements.team.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Name")
@Description({"Represents the name and display name of a team.",
    "\nNOTE: Display name can be set, name cannot be set."})
@Examples({"set team display name of {_team} to \"The Warriors\"",
    "set team display name of team of player to \"The Rednecks\"",
    "set team display name of team named \"blue-team\" to \"Blue Team\"",
    "set {_name} to team name of team of player"})
@Since("2.10.0")
@SuppressWarnings("deprecation")
public class ExprTeamName extends SimplePropertyExpression<Team, String> {

    static {
        register(ExprTeamName.class, String.class, "team [:display] name", "teams");
    }

    private boolean display;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.display = parseResult.hasTag("display");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable String convert(Team team) {
        return this.display ? team.getDisplayName() : team.getName();
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (this.display && mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta == null) {
            error("Team name cannot be empty");
            return;
        }
        if (!(delta[0] instanceof String name)) {
            error("Team name has to be a string: " + Classes.toString(delta[0]));
            return;
        }
        for (Team team : getExpr().getArray(event)) {
            team.setDisplayName(name);
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.display ? "team display name" : "team name";
    }

}
