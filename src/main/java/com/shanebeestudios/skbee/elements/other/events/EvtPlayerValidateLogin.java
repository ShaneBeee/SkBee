package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.shanebeee.skr.Registration;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.connection.PlayerLoginConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class EvtPlayerValidateLogin extends SkriptEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtPlayerValidateLogin.class, PlayerConnectionValidateLoginEvent.class,
                "player connection validate (:login|config[uration])",
                "connection validate (:login|config[uration])")
            .name("Player Connection Validate Login")
            .description("Validates whether a player connection is able to log in.",
                "Called when a player is attempting to log in for the first time, or is finishing up being configured.",
                "The base event may be called more than once therefor split up for login/config.")
            .examples("on player connection validate login:",
                "\tif event-connection is transferred:",
                "\t\tretrieve cookie with key \"transfer\" from event-connection:",
                "\t\t\tif transfer cookie = \"%event-uuid%-transfer\":",
                "\t\t\t\tstop",
                "\t\tdisconnect event-connection due to \"<red>ILLEGAL TRANSFER\"")
            .since("3.25.0")
            .register();

        reg.newEventValue(PlayerConnectionValidateLoginEvent.class, PlayerConnection.class)
            .description("Represents the connection of the player in this event.")
            .patterns("connection")
            .converter(PlayerConnectionValidateLoginEvent::getConnection)
            .register();
        reg.newEventValue(PlayerConnectionValidateLoginEvent.class, Audience.class)
            .description("Represents the audience of the player in config mode. This may be null.")
            .converter(event -> {
                if (event.getConnection() instanceof PlayerConfigurationConnection connection) {
                    return connection.getAudience();
                }
                return null;
            })
            .register();
        reg.newEventValue(PlayerConnectionValidateLoginEvent.class, UUID.class)
            .description("Represents the UUID of the player in this event. This may be null.")
            .converter(event -> {
                if (event.getConnection() instanceof PlayerConfigurationConnection connection) {
                    return connection.getProfile().getId();
                } else if (event.getConnection() instanceof PlayerLoginConnection connection) {
                    PlayerProfile profile = connection.getAuthenticatedProfile();
                    if (profile == null) {
                        profile = connection.getUnsafeProfile();
                    }
                    if (profile != null) {
                        return profile.getId();
                    }
                }
                return null;
            })
            .register();
    }

    private boolean login;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.login = parseResult.hasTag("login");
        return true;
    }

    @Override
    public boolean check(Event event) {
        PlayerConnectionValidateLoginEvent validateEvent = (PlayerConnectionValidateLoginEvent) event;
        PlayerConnection connection = validateEvent.getConnection();
        if (this.login && connection instanceof PlayerLoginConnection) {
            return true;
        } else if (!this.login && connection instanceof PlayerConfigurationConnection) {
            return true;
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String log = this.login ? "login" : "config";
        return "player connection validate " + log;
    }

}
