package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Recipe with Id")
@Description({"Gets a minecraft recipe based off a given recipe id.",
        "If no recipe key value is given (i.e. 'skbee' within 'skbee:my_recipe') we default to config recipe key"})
@Examples({"set {_recipe} to recipe from id \"minecraft:oak_door\"",
        "set {_recipe} to recipe with id \"skbee:my_recipe\"",
        "set {_recipes::*} to recipe with ids \"someplug:recipe_name\", \"my_recipe\"",
        "set {_result} to recipe result of {_recipe}"})
@Since("INSERT VERSION")
public class ExprRecipeWithId extends SimpleExpression<Recipe> {

    static {
        Skript.registerExpression(ExprRecipeWithId.class, Recipe.class, ExpressionType.SIMPLE, "recipe[s] (using|(with|from) id[s]) %strings/namespacedkeys%");
    }

    private Expression<Object> recipeIds;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        recipeIds = (Expression<Object>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable Recipe[] get(Event event) {
        List<Recipe> recipes = new ArrayList<>();
        for (Object recipeId : recipeIds.getArray(event)) {
            NamespacedKey recipeKey = RecipeUtil.getKey(recipeId);
            if (recipeKey != null) recipes.add(Bukkit.getRecipe(recipeKey));
        }
        return recipes.toArray(new Recipe[0]);
    }

    @Override
    public boolean isSingle() {
        return recipeIds.isSingle();
    }

    @Override
    public Class<? extends Recipe> getReturnType() {
        return Recipe.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "recipe from id " + recipeIds.toString(event, debug);
    }

}

