package com.shanebeestudios.skbee.api.event.recipe;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class ShapedRecipeCreateEvent extends Event {

    private final ShapedRecipe recipe;

    public ShapedRecipeCreateEvent(ShapedRecipe recipe) {
        this.recipe = recipe;
    }

    public ShapedRecipe getRecipe() {
        return recipe;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("This event should be not called!");
    }

}
