package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Recipe ID")
@Description({"Gets the identifier of a recipe",
		"Requires Minecraft 1.13+"})
@Examples({"set {_recipe} to random element out of recipes",
		"send recipe id of {_recipe}"
})
@Since("INSERT VERSION")
public class ExprRecipeId extends SimplePropertyExpression<Recipe,String> {


	@Override
	public @Nullable String convert(Recipe recipe) {
		if (recipe instanceof Keyed keyed)
			return keyed.getKey().toString();
		return null;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "recipe id";
	}
}
