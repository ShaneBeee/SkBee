package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.util.SimpleEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class EvtRecipe extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtRecipe.class, PlayerRecipeDiscoverEvent.class,
                "recipe discover[y]")
            .name("Recipe - Discover Event")
            .description("Called when a player unlocks a recipe.")
            .examples("on recipe discover:",
                "\tif event-string = \"minecraft:diamond_block\"",
                "\t\tcancel event")
            .since("1.0.0")
            .register();

        reg.newEventValue(PlayerRecipeDiscoverEvent.class, String.class)
            .description("The recipe NamespacedKey as a string (this will also include either \"minecraft:\" or \"mykeyhere:\").")
            .converter(event -> event.getRecipe().toString())
            .register();
        reg.newEventValue(PlayerRecipeDiscoverEvent.class, NamespacedKey.class)
            .description("The recipe NamespacedKey (this will also include either \"minecraft:\" or \"mykeyhere:\").")
            .converter(PlayerRecipeDiscoverEvent::getRecipe)
            .register();
        reg.newEventValue(PlayerRecipeDiscoverEvent.class, Boolean.class)
            .description("Whether or not to show a notification (toast) to the player.")
            .converter(PlayerRecipeDiscoverEvent::shouldShowNotification)
            .changer(Changer.ChangeMode.SET, PlayerRecipeDiscoverEvent::shouldShowNotification)
            .register();

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
            .since("3.6.1")
            .register();

        reg.newEventValue(CrafterCraftEvent.class, String.class)
            .converter(event -> event.getRecipe().getKey().toString())
            .register();
    }

}
