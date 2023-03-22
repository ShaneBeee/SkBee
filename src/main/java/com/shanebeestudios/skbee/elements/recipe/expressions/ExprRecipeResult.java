package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Result")
@Description({"Get the result item of a recipe.",
        "\nID = Minecraft or custom NamespacedKey, see examples."})
@Examples({"set {_result} to recipe result of recipe with id \"minecraft:oak_door\"",
        "set {_result} to recipe result of recipe with id \"skbee:some_recipe\"",
        "set {_result} to recipe result of recipe with id \"my_recipes:some_custom_recipe\""})
@Since("2.6.0")
public class ExprRecipeResult extends SimplePropertyExpression<Recipe, ItemStack> {

    static {
        register(ExprRecipeResult.class, ItemStack.class, "recipe result[s]", "recipes");
    }

    @Override
    @Nullable
    public ItemStack convert(Recipe recipe) {
        return recipe.getResult();
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    protected String getPropertyName() {
        return "recipe result";
    }
}
