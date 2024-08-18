package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.skbee.elements.other.events.OtherEvents;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.jetbrains.annotations.NotNull;

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

        if (Skript.classExists("org.bukkit.event.block.CrafterCraftEvent")) {
            Skript.registerEvent("Recipe - Crafter Craft Event", EvtRecipe.class, CrafterCraftEvent.class, "crafter craft")
                .description("Called when a Crafter is about to craft an item. Requires Minecraft 1.21.1+",
                    "`event-string` = The key for the recipe used in this event.",
                    "`recipe result` = An expression that reprsents the result slot (can be changed).")
                .examples("on crafter craft:",
                    "\tif event-string = \"minecraft:diamond_sword\":",
                    "\t\tset name of recipe result to \"Señor Sword\"",
                    "\telse:",
                    "\t\tset recipe result to a stick named \"&cNice Try\"",
                    "",
                    "on preparing craft:",
                    "\tset {_e} to event-string",
                    "\tif {_e} = \"minecraft:diamond_shovel\":",
                    "\t\tset name of recipe result to \"&cMr Shovel\"")
                .since("3.6.1");

            EventValues.registerEventValue(CrafterCraftEvent.class, String.class, new Getter<>() {
                @Override
                public @NotNull String get(CrafterCraftEvent event) {
                    return event.getRecipe().getKey().toString();
                }
            }, EventValues.TIME_NOW);
        }
    }

}
