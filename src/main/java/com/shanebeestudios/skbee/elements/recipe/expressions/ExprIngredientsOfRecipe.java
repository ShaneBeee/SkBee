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
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

@Name("Recipe - Ingredients of Recipe")
@Description({"Get the ingredients from a recipe.",
        "\nNote: depending on recipe it will return differently i.e shaped recipe will return an ingredient",
        "where smithing recipe will return a recipe choice"})
@Examples({"set {_ing::*} to ingredients of recipe with id \"minecraft:diamond_sword\"",
        "loop recipes for iron ingot:",
        "\tset {_ing::*} to ingredients of loop-value"})
@Since("1.4.0")
public class ExprIngredientsOfRecipe extends PropertyExpression<Recipe, Object> {

    static {
        register(ExprIngredientsOfRecipe.class, Object.class, "[recipe] ingredients", "recipes");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<Recipe>) exprs[0]);
        return true;
    }

    @Override
    @Nullable
    protected Object[] get(Event event, Recipe[] recipes) {
        List<Object> ingredients = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe instanceof MerchantRecipe merchantRecipe) {
                ingredients.addAll(merchantRecipe.getIngredients());
            } else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                ingredients.add(stonecuttingRecipe.getInputChoice());
            } else if (recipe instanceof SmithingRecipe smithingRecipe) {
                ingredients.add(smithingRecipe.getBase());
                ingredients.add(smithingRecipe.getAddition());
            } else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                ingredients.add(cookingRecipe.getInputChoice());
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                ingredients.addAll(shapelessRecipe.getChoiceList());
            } else if (recipe instanceof ShapedRecipe shapedRecipe) {
                for (Entry<Character, RecipeChoice> entry : shapedRecipe.getChoiceMap().entrySet()) {
                    ingredients.add(entry.getValue());
                }
            }
        }
        return ingredients.toArray(new Object[0]);
    }

    @Override
    public Class<?> getReturnType() {
        Class<?> returntype = getExpr().getReturnType();
        return MerchantRecipe.class.isAssignableFrom(returntype) ? ItemStack.class
                : Recipe.class.isAssignableFrom(returntype) ? RecipeChoice.class
                : Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "ingredients of " + getExpr().toString(event, debug);
    }

}
