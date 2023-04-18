package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


// TODO: add recipe ingredient by shape key, makes support for custom shape more usable in long term
@Name("Recipes - Recipe Shape")
@Description({"Gets the registered shape of a Shaped Recipe",
		"Recipe shape will return an array of the shape, while compacted will return a string in an easier to read format.",
		"Requires Minecraft 1.13+"})
@Examples({"send recipe shape of recipe with id \"minecraft:oak_door\" # \"ab\", \"cd\" and \"ef\"",
		"send compacted recipe shape of recipe with id \"minecraft:oak_door\" # \"minecraft:oak_door{ab, cd, ef}\""})
@Since("INSERT VERSION")
public class ExprRecipeShape extends PropertyExpression<Recipe, String> {

	static {
		register(ExprRecipeShape.class, String.class, "[:compacted] recipe shape", "recipes");
	}

	private boolean isCompact;
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		isCompact = parseResult.hasTag("compacted");
		setExpr((Expression<? extends Recipe>) exprs[0]);
		return true;
	}

	@Override
	@Nullable
	protected String[] get(Event event, Recipe[] recipes) {
		List<String> recipeShapes = new ArrayList<>();
		for (Recipe recipe : recipes) {
			if (!(recipe instanceof ShapedRecipe)) continue;
			ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
			if (!isCompact) {
				recipeShapes.addAll(List.of(shapedRecipe.getShape()));
				continue;
			}
			String[] shape = shapedRecipe.getShape();
			String key = shapedRecipe.getKey().asString();
			recipeShapes.add(key + "{" + String.join(", ", shape) + "}");
		}
		return recipeShapes.toArray(new String[0]);
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (this.isCompact ? "compacted " : "") + "recipe shape of " + getExpr().toString(event, debug);
	}

}
