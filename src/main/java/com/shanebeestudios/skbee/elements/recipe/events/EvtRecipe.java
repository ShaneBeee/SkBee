package com.shanebeestudios.skbee.elements.recipe.events;

import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventConverter;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.jspecify.annotations.Nullable;

public class EvtRecipe extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtRecipe.class, PlayerRecipeDiscoverEvent.class,
                "recipe discover[y]")
            .name("Recipe - Discover Event")
            .description("Called when a player unlocks a recipe.",
                "**Event Values**:",
                " - `event-namespacedkey` = The recipe NamespacedKey (this will also include either \"minecraft:\" or \"mykeyhere:\")",
                " - `event-string` = The recipe NamespacedKey as a string (this will also include either \"minecraft:\" or \"mykeyhere:\")",
                " - `event-boolean` = Whether or not to show a notification (toast) to the player (can be set).")
            .examples("on recipe discover:",
                "\tif event-string = \"minecraft:diamond_block\"",
                "\t\tcancel event")
            .since("1.0.0")
            .register();
        reg.registerEventValue(PlayerRecipeDiscoverEvent.class, String.class,
            event -> event.getRecipe().toString(),
            EventValues.TIME_NOW);
        reg.registerEventValue(PlayerRecipeDiscoverEvent.class, NamespacedKey.class,
            PlayerRecipeDiscoverEvent::getRecipe,
            EventValues.TIME_NOW);
        reg.registerEventValue(PlayerRecipeDiscoverEvent.class, Boolean.class, new EventConverter<>() {
            @Override
            public void set(PlayerRecipeDiscoverEvent event, @Nullable Boolean value) {
                event.shouldShowNotification(Boolean.TRUE.equals(value));
            }

            @Override
            public Boolean convert(PlayerRecipeDiscoverEvent event) {
                return event.shouldShowNotification();
            }
        }, EventValues.TIME_NOW);

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

        reg.registerEventValue(CrafterCraftEvent.class, String.class, event -> event.getRecipe().getKey().toString(), EventValues.TIME_NOW);
    }

}
