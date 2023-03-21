package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import jdk.jfr.Name;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.eclipse.jdt.annotation.Nullable;

@Name("Recipe - Group of Recipe")
@Description({
		"Get the group of a recipe, not all recipes support groups.",
		"Requires Minecraft 1.13+"
})
@Examples({
		"set {_recipes::*} to recipes where [recipe group of input is \"custom\"]",
		"set {_recipe} to recipe with id \"someplugin:your_recipe\"",
		"set recipe group of {_recipe} to \"some_cool_group\"",
		"send {_recipe}'s recipe group"
})
@Since("INSERT VERSION")
public class ExprRecipeGroup extends SimplePropertyExpression<Recipe, String> {

	static {
		register(ExprRecipeGroup.class, String.class, "recipe group", "recipes");
	}

	@Override
	@Nullable
	public String convert(Recipe recipe) {
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			return shapedRecipe.getGroup();
		}
		else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			return shapelessRecipe.getGroup();
		}
		else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
			return stonecuttingRecipe.getGroup();
		}
		else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
			return cookingRecipe.getGroup();
		}
		return null;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "recipe group";
	}
}
