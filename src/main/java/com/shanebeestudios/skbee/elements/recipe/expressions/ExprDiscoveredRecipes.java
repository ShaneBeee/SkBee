package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipes - Discovered")
@Description({
		"Get and modify the discovered recipes of a player",
		"Requires minecraft 1.13+"
})
@Examples({
		"clear discovered recipes of player",
		"add recipe with id \"someplugin:your_recipe\" to discovered recipes of player"
})
@Since("INSERT VERSION")
public class ExprDiscoveredRecipes extends PropertyExpression<Player, Recipe> {

	static {
		register(ExprDiscoveredRecipes.class, Recipe.class, "discovered recipes", "players");
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Player>) exprs[0]);
		return true;
	}

	@Override
	protected Recipe[] get(Event event, Player[] players) {
		List<Recipe> recipes = new ArrayList<>();
		for (Player player : players) {
			for (NamespacedKey namespacedKey : player.getDiscoveredRecipes()) {
				Recipe recipe = Bukkit.getRecipe(namespacedKey);
				if (!recipes.contains(recipe))
					recipes.add(recipe);
			}
		}
		return recipes.toArray(new Recipe[0]);
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		switch (mode) {
			case ADD, REMOVE, SET, RESET, DELETE -> {
				return CollectionUtils.array(Recipe[].class);
			}
		}
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		switch (mode) {
			case RESET, DELETE:
				for (Player player : getExpr().getArray(event)) {
					player.undiscoverRecipes(player.getDiscoveredRecipes());
				}
				break;
			case SET:
				for (Player player : getExpr().getArray(event)) {
					player.undiscoverRecipes(player.getDiscoveredRecipes());
					for (Object object : delta) {
						if (object instanceof Recipe recipe && recipe instanceof Keyed keyed)
							player.discoverRecipe(keyed.getKey());
					}
				}
				break;
			case ADD:
				for (Player player : getExpr().getArray(event)) {
					for (Object object : delta) {
						if (object instanceof Recipe recipe && recipe instanceof Keyed keyed)
							if (!player.hasDiscoveredRecipe(keyed.getKey()))
								player.discoverRecipe(keyed.getKey());
					}
				}
				break;
			case REMOVE:
				for (Player player : getExpr().getArray(event)) {
					for (Object object : delta) {
						if (object instanceof Recipe recipe && recipe instanceof Keyed keyed)
							if (player.hasDiscoveredRecipe(keyed.getKey()))
								player.undiscoverRecipe(keyed.getKey());
					}
				}
				break;

		}
	}

	@Override
	public Class<? extends Recipe> getReturnType() {
		return Recipe.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "discovered recipes of " + getExpr().toString(event, debug);
	}
}
