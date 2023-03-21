package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems"})
@Name("Recipe - All Recipes")
@Description({
        "Get a list of all recipes. May be from a specific item, may be just Minecraft recipes or custom recipes.",
        "Due to some items having more than 1 recipe this may return multiple recipes. Requires 1.13+"
})
@Examples("set {_recipes::*} to all recipes of iron ingot")
@Since("1.4.0")
public class ExprAllRecipes extends SimpleExpression<Recipe> {

    static {
        Skript.registerExpression(ExprAllRecipes.class, Recipe.class, ExpressionType.COMBINED,
                "[(all [[of] the]|the)] [(1:(mc|minecraft)|2:custom)] recipe[s] [(for|of) %-itemstack%]");
    }

    private int pattern;
    @Nullable
    private Expression<ItemStack> items;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = parseResult.mark;
        items = (Expression<ItemStack>) exprs[0];
        return true;
    }

    @Override
    protected Recipe[] get(Event event) {
        List<Recipe> recipes = new ArrayList<>();
        if (this.items != null) {
            for (ItemStack item : items.getArray(event)) {
                for (Recipe recipe : Bukkit.getRecipesFor(item)) {
                    NamespacedKey namespacedKey = ((Keyed) recipe).getKey();
                    if (pattern == 0 || isMinecraft(namespacedKey) || isCustom(namespacedKey)) {
                        recipes.add(recipe);
                    }
                }
            }
        } else {
            Bukkit.recipeIterator().forEachRemaining(recipe -> {
                NamespacedKey namespacedKey = ((Keyed) recipe).getKey();
                if (pattern == 0 || isMinecraft(namespacedKey) || isCustom(namespacedKey)) {
                    recipes.add(recipe);
                }
            });
        }
        return recipes.toArray(new Recipe[0]);
    }

    private boolean isMinecraft(NamespacedKey key) {
        return pattern == 1 && key.getNamespace().equalsIgnoreCase("minecraft");
    }

    private boolean isCustom(NamespacedKey key) {
        return pattern == 2 && !key.getNamespace().equalsIgnoreCase("minecraft");
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return Recipe.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (items != null) {
            return "all recipes for " + items.toString(event, debug);
        }
        return "all recipes";
    }
}
