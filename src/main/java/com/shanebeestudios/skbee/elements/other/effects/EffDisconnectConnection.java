package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EffDisconnectConnection extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffDisconnectConnection.class, "disconnect %players/playerconnections% [due to %-textcomponent%]")
            .name("Disconnect Connection")
            .description("Disconnect a player or player connection from the server with an optional message.")
            .examples("on player connection validate login:",
                "\tif event-connection is transferred:",
                "\t\tretrieve cookie with key \"transfer\" from event-connection:",
                "\t\t\tif transfer cookie = \"%event-uuid%-transfer\":",
                "\t\t\t\tstop",
                "\t\tdisconnect event-connection due to \"<red>ILLEGAL TRANSFER\"")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> players;
    private Expression<TextComponent> reason;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = expressions[0];
        this.reason = (Expression<TextComponent>) expressions[1];
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void execute(Event event) {
        TextComponent reason = this.reason.getSingle(event);

        for (Object o : this.players.getArray(event)) {
            if (o instanceof PlayerConnection connection) {
                connection.disconnect(Objects.requireNonNullElseGet(reason, Component::empty));
            } else if (o instanceof Player player) {
                player.kick(reason);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String reason = this.reason != null ? " due to " + this.reason.toString(event, debug) : "";
        return "disconnect " + this.players.toString(event, debug) + reason;
    }

}
