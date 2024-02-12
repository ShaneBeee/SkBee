package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class EvtRecipe extends SimpleEvent {

    static {
        Skript.registerEvent("Recipe - Discover Event", EvtRecipe.class, PlayerRecipeDiscoverEvent.class,
                        "recipe discover[y]")
                .description("Called when a player unlocks a recipe. ",
                        "`event-string` = the recipe namespace (this will also include either \"minecraft:\" or \"mykeyhere:\")",
                        "Requires MC 1.13+")
                .examples("on recipe discover:",
                        "\tif event-string = \"minecraft:diamond_block\"",
                        "\t\tcancel event")
                .requiredPlugins("1.13+")
                .since("1.0.0");
        EventValues.registerEventValue(PlayerRecipeDiscoverEvent.class, String.class, new Getter<String, PlayerRecipeDiscoverEvent>() {
            @Override
            public String get(PlayerRecipeDiscoverEvent event) {
                return event.getRecipe().toString();
            }
        }, 0);
    }

}
