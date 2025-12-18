package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.audience.Audience;

import java.util.UUID;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class AsyncEvents extends SimpleEvent {

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }

    static {
        if (Skript.classExists("io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent")) {
            Skript.registerEvent("Async Player Connection Configure", AsyncEvents.class, AsyncPlayerConnectionConfigureEvent.class,
                    "async player connection configure")
                .description("An event that allows you to configure the player.",
                    "This is async and allows you to run configuration code on the player.",
                    "Once this event has finished execution, the player connection will continue.",
                    "Freezing code within this event will pause the player from logging in.",
                    "**NOTE**: When this event is called, there is no Player object yet, so you will have to rely on name/uuid/audience.",
                    "**Event Values**:",
                    "- `event-uuid` = Uuid of the player who is logging in.",
                    "- `event-string` = Name of the player who is logging in.",
                    "- `event-audience` = The audience represented by the connection.")
                .examples("on async player connection configure:",
                    "\tset {-connect::%event-uuid%} to true",
                    "",
                    "\t# Do something",
                    "",
                    "\twhile {-connect::%event-uuid%} is set:",
                    "\t\t# Player login will be halted while we wait for something",
                    "\t\tsleep thread for 1 tick",
                    "\t#Player will now connect")
                .since("INSERT VERSION");

            EventValues.registerEventValue(AsyncPlayerConnectionConfigureEvent.class, UUID.class,
                event -> event.getConnection().getProfile().getId());
            EventValues.registerEventValue(AsyncPlayerConnectionConfigureEvent.class, String.class,
                event -> event.getConnection().getProfile().getName());
            EventValues.registerEventValue(AsyncPlayerConnectionConfigureEvent.class, Audience.class,
                event -> event.getConnection().getAudience());
        }
    }

}
