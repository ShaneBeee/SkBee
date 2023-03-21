package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.eclipse.jdt.annotation.Nullable;

public class ExprRecipeId extends SimplePropertyExpression<Recipe,String> {


	@Override
	public @Nullable String convert(Recipe recipe) {
		if(recipe instanceof Keyed keyed)
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
