package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.eclipse.jdt.annotation.Nullable;

@Name("Recipe - Category")
@Description("Returns the category of a valid recipe. Requires Minecraft 1.19+")
@Examples({"set {_recipes::*} to recipes with id \"minecraft:oak_planks\", \"skbee:my_recipe\"",
        "broadcast recipe category of {_recipes::*}"})
@Since("INSERT VERSION")
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
        Class<?> returnType = getExpr().getReturnType();
        return ShapedRecipe.class.isAssignableFrom(returnType) ? CraftingBookCategory.class
                : ShapelessRecipe.class.isAssignableFrom(returnType) ? CraftingBookCategory.class
                : CookingRecipe.class.isAssignableFrom(returnType) ? CookingBookCategory.class
                : Object.class;
    }

    @Override
    protected String getPropertyName() {
        return "recipe category";
    }

}
