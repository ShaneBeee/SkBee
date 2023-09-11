package com.shanebeestudios.skbee.api.event.recipe;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class ShapelessRecipeCreateEvent extends Event {

    private final ShapelessRecipe recipe;

    public ShapelessRecipeCreateEvent(ShapelessRecipe recipe) {
        this.recipe = recipe;
    }

    public ShapelessRecipe getRecipe() {
        return recipe;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("This event should be not called!");
    }

}
