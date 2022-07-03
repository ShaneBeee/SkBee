package com.shanebeestudios.skbee.elements.advancement.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SimpleEvents {

    static {
        // Player Advancement Event
        Skript.registerEvent("Player Advancement", SimpleEvent.class, PlayerAdvancementDoneEvent.class,
                        "[player] advancement done")
                .description("Called when a player has completed all criteria in an advancement.")
                .examples("")
                .since("1.17.0");

        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, String.class, new Getter<>() {
            @Override
            public @Nullable String get(PlayerAdvancementDoneEvent event) {
                return event.getAdvancement().getKey().toString();
            }
        }, 0);

        EventValues.registerEventValue(PlayerAdvancementDoneEvent.class, Advancement.class, new Getter<>() {
            @Override
            public @Nullable Advancement get(PlayerAdvancementDoneEvent event) {
                return event.getAdvancement();
            }
        }, 0);
    }

}
