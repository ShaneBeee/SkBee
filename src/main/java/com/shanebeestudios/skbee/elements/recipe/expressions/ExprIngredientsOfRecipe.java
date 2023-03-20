package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
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
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"NullableProblems", "deprecation"})
@Name("Recipe - Ingredients of Recipe")
@Description("Get the ingredients from a recipe. Requires 1.13+")
@Examples({
        "set {_ing::*} to ingredients of recipe with id \"minecraft:diamond_sword\"",
        "loop recipes for iron ingot:",
        "\tset {_ing::*} to ingredients of loop-value"})
@Since("1.4.0")
public class ExprIngredientsOfRecipe extends PropertyExpression<Recipe, ItemStack> {

    static {
        register(ExprIngredientsOfRecipe.class, ItemStack.class, "[recipe] ingredients", "recipes");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<Recipe>) exprs[0]);
        return true;
    }

    @Override
    protected ItemStack[] get(Event event, Recipe[] recipes) {
        List<ItemStack> items = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if(recipe instanceof MerchantRecipe merchantRecipe) {
                items.addAll(merchantRecipe.getIngredients());
            }
            else if(recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                items.add(RecipeUtil.getItemStack(stonecuttingRecipe.getInputChoice()));
            }
            else if(recipe instanceof SmithingRecipe smithingRecipe) {
                items.add(RecipeUtil.getItemStack(smithingRecipe.getBase()));
                items.add(RecipeUtil.getItemStack(smithingRecipe.getAddition()));
            }
            else if(recipe instanceof CookingRecipe<?> cookingRecipe) {
                items.add(RecipeUtil.getItemStack(cookingRecipe.getInputChoice()));
            }
            else if(recipe instanceof ShapelessRecipe shapelessRecipe) {
                for (RecipeChoice recipeChoice : shapelessRecipe.getChoiceList()) {
                    items.add(RecipeUtil.getItemStack(recipeChoice));
                }
            }
            else if(recipe instanceof ShapedRecipe shapedRecipe) {
                for (Map.Entry<Character, RecipeChoice> entry : shapedRecipe.getChoiceMap().entrySet()) {
                    items.add(RecipeUtil.getItemStack(entry.getValue()));
                }
            }
        }
        return items.toArray(new ItemStack[0]);
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "ingredients of " + getExpr().toString(event, debug);
    }
}
