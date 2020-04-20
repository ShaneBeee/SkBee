package tk.shanebee.bee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import tk.shanebee.bee.api.util.PlayerUtils;

@Name("Simplified Debug Screen")
@Description("This effect allows you to reduce a big part of the player's debug screen." +
        "This can be useful for survival servers where you dont want your players to know their coords.")
@Examples({"on join:", "\treduce debug screen for player"})
@Since("INSERT VERSION")
public class EffHideDebug extends Effect {

    static {
        Skript.registerEffect(EffHideDebug.class,
                "(0¦reduce|1¦expand) debug [screen] for %players%");
    }

    private Expression<Player> players;
    private boolean hide;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        hide = parseResult.mark == 1;
        return true;
    }

    @Override
    protected void execute(Event e) {
        for (Player player : this.players.getAll(e)) {
            if (hide) {
                PlayerUtils.disableF3(player);
            } else {
                PlayerUtils.enableF3(player);
            }
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return (hide ? "hide" : "show") + " debug screen for " + this.players.toString(e, d);
    }

}
