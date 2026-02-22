package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class EvtRecipe extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtRecipe.class, PlayerRecipeDiscoverEvent.class,
                "recipe discover[y]")
            .name("Recipe - Discover Event")
            .description("Called when a player unlocks a recipe. ",
                "`event-string` = the recipe namespace (this will also include either \"minecraft:\" or \"mykeyhere:\")",
                "Requires MC 1.13+")
            .examples("on recipe discover:",
                "\tif event-string = \"minecraft:diamond_block\"",
                "\t\tcancel event")
            .since("1.0.0")
            .register();
        EventValues.registerEventValue(PlayerRecipeDiscoverEvent.class, String.class, event -> event.getRecipe().toString(), EventValues.TIME_NOW);

        reg.newEvent(EvtRecipe.class, CrafterCraftEvent.class, "crafter craft")
            .name("Recipe - Crafter Craft Event")
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

        EventValues.registerEventValue(CrafterCraftEvent.class, String.class, event -> event.getRecipe().getKey().toString(), EventValues.TIME_NOW);
    }

}
