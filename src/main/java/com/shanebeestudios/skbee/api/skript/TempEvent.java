package com.shanebeestudios.skbee.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.elements.other.events.PaperEvents;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class TempEvent {

    public static void loadEvent() {
        Skript.registerEvent("Player Fail Move", PaperEvents.class, PlayerFailMoveEvent.class, "player fail move")
            .description("Called when a player attempts to move, but is prevented from doing so by the server.",
                "Requires PaperMC and Skript 2.11+.",
                "`event-failmovereason` = The reason they failed to move.",
                "`event-location` = The location they moved from.",
                "`future event-location` = The location they moved to.",
                "`event-boolean` = Whether the player is allowed to move (can be set).",
                "`future event-boolean` = Whether to log warning to console (can be set).")
            .examples("on player fail move:",
                "\tset event-boolean to true",
                "\tset future event-boolean to false",
                "\tif event-failmovereason = clipped_into_block:",
                "\t\tpush player up with speed 1")
            .since("3.11.0");

        EventValues.registerEventValue(PlayerFailMoveEvent.class, PlayerFailMoveEvent.FailReason.class, PlayerFailMoveEvent::getFailReason);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getFrom, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Location.class, PlayerFailMoveEvent::getTo, EventValues.TIME_FUTURE);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setAllowed(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.isAllowed();
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerFailMoveEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerFailMoveEvent event, @Nullable Boolean allowed) {
                event.setLogWarning(Boolean.TRUE.equals(allowed));
            }

            @Override
            public Boolean convert(PlayerFailMoveEvent event) {
                return event.getLogWarning();
            }
        }, EventValues.TIME_FUTURE);
    }

}
