package com.shanebeestudios.skbee.elements.advancement.event;

import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
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

        reg.newEventValue(PlayerAdvancementDoneEvent.class, String.class)
            .converter(event -> event.getAdvancement().getKey().toString())
            .register();
        reg.newEventValue(PlayerAdvancementDoneEvent.class, Advancement.class)
            .converter(PlayerAdvancementDoneEvent::getAdvancement)
            .register();
    }

}
