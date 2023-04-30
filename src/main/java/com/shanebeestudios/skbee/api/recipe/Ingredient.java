package com.shanebeestudios.skbee.api.recipe;

import org.bukkit.inventory.RecipeChoice;

public record Ingredient(char key, RecipeChoice recipeChoice) {

    @Override
    public String toString() {
        return key + ":" + RecipeUtil.recipeChoiceToString(recipeChoice);
    }

}
