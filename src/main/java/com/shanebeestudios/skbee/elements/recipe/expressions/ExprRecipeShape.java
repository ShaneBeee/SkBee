package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.StringUtils;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.eclipse.jdt.annotation.Nullable;


@NoDoc // Marked as NoDoc until advanced recipes are created, this is currently just a debug syntax
@Name("Recipes - Recipe Shape")
@Description({
		"",
		"Requires Minecraft 1.13+"
})
@Examples("send recipe shape of recipe with id \"minecraft:oak_door\"")
@Since("INSERT VERSION")
public class ExprRecipeShape extends SimplePropertyExpression<Recipe, String> {

	static {
		register(ExprRecipeShape.class, String.class, "recipe shape", "recipes");
	}

	@Override
	public @Nullable String convert(Recipe recipe) {
		if(recipe instanceof ShapedRecipe shapedRecipe)
			return String.format("%s{shape=[%s]}", shapedRecipe.getKey(), StringUtils.join(shapedRecipe.getShape(), "], ["));
		return null;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "recipe shape";
	}
}
