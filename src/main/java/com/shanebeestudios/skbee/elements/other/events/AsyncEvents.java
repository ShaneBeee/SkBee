package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.audience.Audience;

import java.util.UUID;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class AsyncEvents extends SimpleEvent {

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }

    public static void register(Registration reg) {
        if (Skript.classExists("io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent")) {
            reg.newEvent(AsyncEvents.class, AsyncPlayerConnectionConfigureEvent.class,
                    "async player connection configure")
                .name("Async Player Connection Configure")
                .description("An event that allows you to configure the player.",
                    "This is async and allows you to run configuration code on the player.",
                    "Once this event has finished execution, the player connection will continue.",
                    "Freezing code within this event will pause the player from logging in.",
                    "**NOTE**: When this event is called, there is no Player object yet, so you will have to rely on name/uuid/audience.")
                .examples("on async player connection configure:",
                    "\tset {-connect::%event-uuid%} to true",
                    "",
                    "\t# Do something",
                    "",
                    "\twhile {-connect::%event-uuid%} is set:",
                    "\t\t# Player login will be halted while we wait for something",
                    "\t\tsleep thread for 1 tick",
                    "\t#Player will now connect")
                .since("3.15.0")
                .register();

            reg.newEventValue(AsyncPlayerConnectionConfigureEvent.class, UUID.class)
                .description("Uuid of the player who is logging in.")
                .converter(event -> event.getConnection().getProfile().getId())
                .register();
            reg.newEventValue(AsyncPlayerConnectionConfigureEvent.class, String.class)
                .description("Name of the player who is logging in.")
                .converter(event -> event.getConnection().getProfile().getName())
                .register();
            reg.newEventValue(AsyncPlayerConnectionConfigureEvent.class, Audience.class)
                .description("The audience represented by the connection.")
                .converter(event -> event.getConnection().getAudience())
                .register();
            reg.newEventValue(AsyncPlayerConnectionConfigureEvent.class, PlayerConnection.class)
                .description("The connection of the player who is logging in.")
                .converter(AsyncPlayerConnectionConfigureEvent::getConnection)
                .register();
        }
    }

}
