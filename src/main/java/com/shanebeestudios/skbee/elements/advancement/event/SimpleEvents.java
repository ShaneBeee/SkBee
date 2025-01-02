package com.shanebeestudios.skbee.elements.advancement.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

@SuppressWarnings("unused")
public class SimpleEvents extends SimpleEvent {

    static {
        // Player Advancement Event
        Skript.registerEvent("Player Advancement", SimpleEvents.class, PlayerAdvancementDoneEvent.class,
                "[player] advancement done")
            .description("Called when a player has completed all criteria in an advancement.")
            .examples("")
            .since("1.17.0");

        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, String.class, event -> event.getAdvancement().getKey().toString(), EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, Advancement.class, PlayerAdvancementDoneEvent::getAdvancement, EventValues.TIME_NOW);
    }

}
