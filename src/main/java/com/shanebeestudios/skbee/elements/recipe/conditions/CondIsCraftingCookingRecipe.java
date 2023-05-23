package com.shanebeestudios.skbee.elements.recipe.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@Name("Recipe - Is Cooking Or Crafting")
@Description("Checks if a recipe is a crafting or cooking recipe")
@Examples({"set {_recipes::*} to all recipes where [input is crafting recipe]",
        "set {_recipes::*} to all recipes where [input is a cooking recipe]"})
@Since("INSERT VERSION")
public class CondIsCraftingCookingRecipe extends PropertyCondition<Recipe> {

    static {
        register(CondIsCraftingCookingRecipe.class, "[a] (crafting|:cooking) recipe[s]", "recipes");
    }

    private boolean isCooking;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        isCooking = parseResult.hasTag("cooking");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(Recipe recipe) {
        return isCooking ? recipe instanceof CookingRecipe<?> : isCrafting(recipe);
    }

    private boolean isCrafting(Recipe recipe) {
        return recipe instanceof ShapelessRecipe || recipe instanceof ShapedRecipe || recipe instanceof ComplexRecipe;
    }

    @Override
    protected String getPropertyName() {
        return null;
    }
}
