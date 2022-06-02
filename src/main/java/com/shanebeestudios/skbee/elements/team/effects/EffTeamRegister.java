package com.shanebeestudios.skbee.elements.team.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.team.type.TeamManager;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Team - Register")
@Description({"Register a new team or unregister an existing team.",
        "NOTE: You can also use the team expression to get a team, which will register a new team",
        "if that team does not exist already."})
@Examples({"on load:",
        "\tregister new team \"a-team\""})
@Since("1.16.0")
public class EffTeamRegister extends Effect {

    static {
        Skript.registerEffect(EffTeamRegister.class, "(|1¦un)register [new] team %string%");
    }

    private Expression<String> name;
    private boolean register;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        this.register = parseResult.mark == 0;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        String name = this.name.getSingle(event);
        if (name == null) return;

        if (register) {
            TeamManager.getTeam(name);
        } else {
            TeamManager.unregisterTeam(name);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        String reg = register ? "" : "un";
        return reg + "register team " + this.name.toString(e, d);
    }

}
