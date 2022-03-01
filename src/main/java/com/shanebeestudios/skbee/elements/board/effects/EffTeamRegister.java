package com.shanebeestudios.skbee.elements.board.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.board.objects.BeeTeams;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Team - Register")
@Description("Register a new team or unregister an existing team. NOTE: Teams are not persistent and will need to created on each server start.")
@Examples({"on load:",
        "\tregister new team \"a-team\""})
@Since("1.15.0")
public class EffTeamRegister extends Effect {

    private static final BeeTeams BEE_TEAMS;

    static {
        BEE_TEAMS = SkBee.getPlugin().getBeeTeams();
        Skript.registerEffect(EffTeamRegister.class, "(|1¦un)register [new] [[sk]bee[ ]]team %string%");
    }

    private Expression<String> name;
    private boolean register;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        this.register = parseResult.mark == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        String name = this.name.getSingle(event);
        if (name != null) {
            if (register) {
                BEE_TEAMS.registerTeam(name);
            } else {
                BEE_TEAMS.unregisterTeam(name);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String reg = register ? "register" : "unregister";
        return reg + " team " + this.name.toString(e, d);
    }

}
