package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.eclipse.jdt.annotation.Nullable;

@Name("Recipe - Category")
@Description("Returns the category of a valid recipe")
@Examples({"set {_recipes::*} to recipes with id \"minecraft:oak_planks\", \"skbee:my_recipe\"",
        "broadcast recipe category of {_recipes::*}"})
@Since("INSERT VERSION")
@RequiredPlugins("MC 1.19+")
public class ExprRecipeCategory extends SimplePropertyExpression<Recipe, Object> {

    private static final boolean CATEGORIES_EXIST = Skript.classExists("org.bukkit.inventory.recipe.CraftingBookCategory");

    static {
        if (CATEGORIES_EXIST)
            register(ExprRecipeCategory.class, Object.class, "recipe category", "recipes");
    }

    @Override
    public @Nullable Object convert(Recipe recipe) {
        if (recipe instanceof CookingRecipe<?> cookingRecipe)
            return cookingRecipe.getCategory();
        else if (recipe instanceof ShapedRecipe shapedRecipe)
            return shapedRecipe.getCategory();
        else if (recipe instanceof ShapelessRecipe shapelessRecipe)
            return shapelessRecipe.getCategory();
        return null;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    protected String getPropertyName() {
        return "recipe category";
    }

}
