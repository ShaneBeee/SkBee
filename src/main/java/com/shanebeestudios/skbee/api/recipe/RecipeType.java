package com.shanebeestudios.skbee.api.recipe;

import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.Nullable;

/**
 * Util class defining types of {@link Recipe recipes}
 */
public enum RecipeType {

    SHAPED_RECIPE(ShapedRecipe.class),
    SHAPELESS_RECIPE(ShapelessRecipe.class),
    BLASTING_RECIPE(BlastingRecipe.class),
    CAMPFIRE_RECIPE(CampfireRecipe.class),
    FURNACE_RECIPE(FurnaceRecipe.class),
    MERCHANT_RECIPE(MerchantRecipe.class),
    SMITHING_RECIPE(SmithingRecipe.class),
    SMOKING_RECIPE(SmokingRecipe.class),
    STONECUTTING_RECIPE(StonecuttingRecipe.class),
    // Represents a complex recipe which has imperative server-defined behavior, eg: armor dyeing.
    COMPLEX_RECIPE(ComplexRecipe.class);

    private final Class<? extends Recipe> recipeClass;

    RecipeType(Class<? extends Recipe> recipeClass) {
        this.recipeClass = recipeClass;
    }

    public Class<? extends Recipe> getRecipeClass() {
        return recipeClass;
    }

    @Nullable
    public static RecipeType getFromRecipe(Recipe recipe) {
        Class<? extends Recipe> recipeClass = recipe.getClass();
        for (RecipeType recipeType : values()) {
            if (recipeType.getRecipeClass().isAssignableFrom(recipe.getClass())) {
                return recipeType;
            }
        }
        String key = "invalid";
        if (recipe instanceof Keyed keyed) {
            key = keyed.getKey().toString();
        }
        Util.debug("Missing RecipeType for recipe '%s' with class '%s'", key, recipeClass);
        return null;
    }

}
