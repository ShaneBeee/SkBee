package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Name("Recipe - All Recipes")
@Description({"Get a list of all recipes. May be from a specific item, may be just Minecraft recipes or custom recipes.",
        "Due to some items having more than 1 recipe this may return multiple recipes. Requires 1.13+"})
@Examples({
        "set {_recipes::*} to all recipes",
        "set {_customRecipes::*} to all custom recipes",
        "set {_furnaceRecipes::*} to all furnace recipes",
        "set {_goldIngotRecipes::*} to all recipes for gold ingot",
        "set {_furnaceRecipesForGoldIngot::*} to all furnace recipes for gold ingot"
})
@Since("1.4.0, INSERT VERSION (recipe type definition)")
public class ExprAllRecipes extends SimpleExpression<Recipe> {

    static {
        Skript.registerExpression(ExprAllRecipes.class, Recipe.class, ExpressionType.COMBINED,
                "all [[of] the] [1:(mc|minecraft)|2:custom] ([:cooking|:crafting] recipe[s]|%-recipetype%[s]) [(for|of) %-itemtypes%]");
    }

    private int pattern;
    @Nullable
    private Expression<RecipeType> recipeType;
    @Nullable
    private Expression<ItemType> items;
    private boolean isCooking;
    private boolean isCrafting;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = parseResult.mark;
        items = (Expression<ItemType>) exprs[1];
        recipeType = (Expression<RecipeType>) exprs[0];
        isCooking = parseResult.hasTag("cooking");
        isCrafting = parseResult.hasTag("crafting");
        return true;
    }

    @Override
    protected Recipe[] get(Event event) {

        List<Recipe> recipes = new ArrayList<>();
        RecipeType recipeType = this.recipeType != null ? this.recipeType.getSingle(event) : null;
        if (this.items != null) {
            for (ItemType itemType : items.getArray(event)) {
                for (ItemStack itemStack : itemType.getAll()) {
                    for (Recipe recipe : Bukkit.getRecipesFor(itemStack)) {
                        if (recipeType != null && RecipeType.getFromRecipe(recipe) != recipeType) continue;
                        if (recipeType == null && isCrafting && !isCrafting(recipe)) continue;
                        if (recipeType == null && isCooking && !(recipe instanceof CookingRecipe<?>)) continue;
                        recipes.add(recipe);
                    }
                }
            }
        } else {
            for (Iterator<Recipe> iteration = Bukkit.recipeIterator(); iteration.hasNext(); ) {
                Recipe recipe = iteration.next();
                if (recipeType != null && RecipeType.getFromRecipe(recipe) != recipeType) continue;
                if (recipeType == null && isCrafting && !isCrafting(recipe)) continue;
                if (recipeType == null && isCooking && !(recipe instanceof CookingRecipe<?>)) continue;
                recipes.add(recipe);
            }
        }
        return recipes.stream().
                filter(recipe -> {
                    NamespacedKey key = ((Keyed) recipe).getKey();
                    return pattern == 0 || isCustom(key) || isMinecraft(key);
                }).toList().toArray(new Recipe[0]);
    }

    private boolean isMinecraft(NamespacedKey key) {
        return pattern == 1 && key.getNamespace().equalsIgnoreCase("minecraft");
    }

    private boolean isCustom(NamespacedKey key) {
        return pattern == 2 && !key.getNamespace().equalsIgnoreCase("minecraft");
    }

    private boolean isCrafting(Recipe recipe) {
        return (recipe instanceof ShapedRecipe ||
                recipe instanceof ShapelessRecipe ||
                recipe instanceof ComplexRecipe);
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
