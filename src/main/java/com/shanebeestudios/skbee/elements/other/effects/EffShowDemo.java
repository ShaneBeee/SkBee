package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffShowDemo extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffShowDemo.class, "show demo screen to %players%")
            .name("Show Demo Screen")
            .description("Shows the demo screen to the player, this screen is normally only seen in the demo version of the game.",
                "Servers can modify the text on this screen using a resource pack.")
            .examples("show demo screen to all players",
                "show demo screen to player")
            .since("1.17.0")
            .register();
    }

    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (Player player : this.players.getArray(event)) {
            player.showDemoScreen();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "show demo screen to " + this.players.toString(e, d);
    }

}
