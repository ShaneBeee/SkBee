package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

public class EvtRecipe {

    static {
        Skript.registerEvent("Recipe - Discover Event", SimpleEvent.class, PlayerRecipeDiscoverEvent.class,
                        "recipe discover[y]")
                .description("Called when a player unlocks a recipe. ",
                        "`event-string` = the recipe namespace (this will also include either \"minecraft:\" or \"mykeyhere:\")",
                        "`event-recipe` = the recipe which you clicked upon",
                        "Requires MC 1.13+")
                .examples("on recipe discover:",
                        "\tif event-string = \"minecraft:diamond_block\"",
                        "\t\tcancel event")
                .since("1.0.0");
        EventValues.registerEventValue(PlayerRecipeDiscoverEvent.class, Recipe.class, new Getter<>() {
            @Override
            public Recipe get(PlayerRecipeDiscoverEvent event) {
                return Bukkit.getRecipe(event.getRecipe());
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerRecipeDiscoverEvent.class, String.class, new Getter<>() {
            @Override
            public String get(PlayerRecipeDiscoverEvent event) {
                return event.getRecipe().asString();
            }
        }, EventValues.TIME_NOW);

        // Player Recipe Book Click Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
            Skript.registerEvent("Recipe Book Click", SimpleEvent.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
                    .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
                    .examples("on recipe book click:",
                            "\tif event-string = \"minecraft:diamond_sword\":",
                            "\t\tcancel event")
                    .since("1.5.0");

            EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, new Getter<>() {
                @Override
                public String get(PlayerRecipeBookClickEvent event) {
                    return event.getRecipe().asString();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, Recipe.class, new Getter<>() {
                @Override
                public Recipe get(PlayerRecipeBookClickEvent event) {
                    return Bukkit.getRecipe(event.getRecipe());
                }
            }, EventValues.TIME_NOW);
        }

        // Player Stonecutter Recipe Select Event
        if (Skript.classExists("io/papermc/paper/event/player/PlayerStonecutterRecipeSelectEvent")) {
            Skript.registerEvent("Stonecutter Recipe Select", SimpleEvent.class, PlayerStonecutterRecipeSelectEvent.class,
                            "[player]")
                    .description("Called when a player selectsa a recipe in the stone cutter inventory, requires Paper 1.16+")
                    .since("INSERT VERSION");

            EventValues.registerEventValue(PlayerStonecutterRecipeSelectEvent.class, Recipe.class, new Getter<>() {
                @Override
                public Recipe get(PlayerStonecutterRecipeSelectEvent event) {
                    return event.getStonecuttingRecipe();
                }
            }, EventValues.TIME_NOW);

            EventValues.registerEventValue(PlayerStonecutterRecipeSelectEvent.class, Inventory.class, new Getter<>() {
                @Override
                public Inventory get(PlayerStonecutterRecipeSelectEvent event) {
                    return event.getStonecutterInventory();
                }
            }, EventValues.TIME_NOW);
        }

    }

}
