package com.shanebeestudios.skbee.elements.recipe;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.recipe.conditions.CondHasDiscoveredRecipe;
import com.shanebeestudios.skbee.elements.recipe.effects.EffCookingRecipe;
import com.shanebeestudios.skbee.elements.recipe.effects.EffCraftingRecipe;
import com.shanebeestudios.skbee.elements.recipe.effects.EffKnowledgeBook;
import com.shanebeestudios.skbee.elements.recipe.effects.EffRecipeDiscovery;
import com.shanebeestudios.skbee.elements.recipe.effects.EffRecipeSetIngredient;
import com.shanebeestudios.skbee.elements.recipe.effects.EffRemoveRecipe;
import com.shanebeestudios.skbee.elements.recipe.effects.EffStonecuttingRecipe;
import com.shanebeestudios.skbee.elements.recipe.events.EvtRecipe;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprAllRecipes;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprCraftingResultFromItems;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprIngredientsOfRecipe;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprMaterialChoice;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprRecipeCookTime;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprRecipeExperience;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprRecipeResult;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprRecipeResultSlot;
import com.shanebeestudios.skbee.elements.recipe.expressions.ExprRecipeType;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeBrewing;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeCooking;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeShaped;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeShapeless;
import com.shanebeestudios.skbee.elements.recipe.sections.SecRecipeSmithing;
import com.shanebeestudios.skbee.elements.recipe.sections.SecTransmuteRecipe;
import com.shanebeestudios.skbee.elements.recipe.type.Types;

public class RecipeElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondHasDiscoveredRecipe.register(reg);

        // EFFECTS
        EffCookingRecipe.register(reg);
        EffCraftingRecipe.register(reg);
        EffKnowledgeBook.register(reg);
        EffRecipeDiscovery.register(reg);
        EffRecipeSetIngredient.register(reg);
        EffRemoveRecipe.register(reg);
        EffStonecuttingRecipe.register(reg);

        // EVENTS
        EvtRecipe.register(reg);

        // EXPRESSIONS
        ExprAllRecipes.register(reg);
        ExprCraftingResultFromItems.register(reg);
        ExprIngredientsOfRecipe.register(reg);
        ExprMaterialChoice.register(reg);
        ExprRecipeCookTime.register(reg);
        ExprRecipeExperience.register(reg);
        ExprRecipeResult.register(reg);
        ExprRecipeResultSlot.register(reg);
        ExprRecipeType.register(reg);

        // SECTIONS
        SecRecipeBrewing.register(reg);
        SecRecipeCooking.register(reg);
        SecRecipeShaped.register(reg);
        SecRecipeShapeless.register(reg);
        SecRecipeSmithing.register(reg);
        SecTransmuteRecipe.register(reg);

        // TYPES
        Types.register(reg);
    }
}
