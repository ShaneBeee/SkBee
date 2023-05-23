package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;


@Name("Recipe - Recipe Type")
@Description({"Gets the recipe type of a given recipe."})
@Examples({"send \"myRecipe: %recipe type of recipe with id \"myrecipe:my_first_skbee_reecipe\"%\"",
        "send \"myRecipe2: %recipe type of recipe with id \"my_first_skbee_reecipe\"%\"",
        "send \"somePlugin: %recipe type of recipe with id \"someplugin:some_recipe_id\"%\"",
        "send \"Minecraft: %recipe type of recipe with id \"minecraft:gold_ingot_from_smelting_raw_gold\"%\""})
@Since("2.6.0")
public class ExprRecipeType extends SimplePropertyExpression<Recipe, RecipeType> {

    static {
        register(ExprRecipeType.class, RecipeType.class, "recipe type", "recipes");
    }

    @Override
    @Nullable
    public RecipeType convert(Recipe recipe) {
        return RecipeType.getFromRecipe(recipe);
    }

    @Override
    public Class<? extends RecipeType> getReturnType() {
        return RecipeType.class;
    }

    @Override
    protected String getPropertyName() {
        return "recipe type";
    }

}
