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
@Description({"Get the type of a recipe.", "\nID = Minecraft or custom NamespacedKey, see examples."})
@Examples({"set {_type} to recipe type of recipe with id \"minecraft:oak_door\"",
        "set {_type} to recipe type of recipe with id \"skbee:some_recipe\"",
        "set {_type} to recipe type of recipe with id \"my_recipes:some_custom_recipe\"",
        "if recipe type of recipe with id \"my_recipes:some_custom_recipe\" = shaped recipe:"})
@Since("2.6.0")
public class ExprRecipeType extends SimplePropertyExpression<Recipe, RecipeType> {

    static {
        register(ExprRecipeType.class, RecipeType.class, "recipe type", "recipes");
    }

    @Override
    public @Nullable RecipeType convert(Recipe recipe) {
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
