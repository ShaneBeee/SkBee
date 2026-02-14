package com.shanebeestudios.skbee.elements.advancement.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

@SuppressWarnings("unused")
public class SimpleEvents extends SimpleEvent {

    public static void register(Registration reg) {
        // Player Advancement Event
        reg.newEvent(SimpleEvents.class, PlayerAdvancementDoneEvent.class,
                "[player] advancement done")
            .name("Player Advancement")
            .description("Called when a player has completed all criteria in an advancement.")
            .examples("on advancement done:",
                "\tif event-advancement is \"minecraft:story/mine_stone\":",
                "\t\tgive player a diamond named \"Stone Age\"")
            .since("1.17.0")
            .register();

        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, String.class, event -> event.getAdvancement().getKey().toString(), EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, Advancement.class, PlayerAdvancementDoneEvent::getAdvancement, EventValues.TIME_NOW);
    }

}
