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
import com.shanebeestudios.skbee.api.recipe.Ingredient;
import com.shanebeestudios.skbee.elements.recipe.sections.SecShapedRecipe;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

@Name("Recipe - Ingredient and Key")
@Description({"Represents an ingredient for a recipe. This is a key value system.",
        "\nKey: a single character, must be a number of letter (Variables will not work here).",
        "\nValue: an item or material choice (see material choice expression for more info)."})
@Examples({"set {_ing} to 1:diamond sword",
        "set {_ing} to d:diamond",
        "set {_ing} to w:white wool",
        "set {_ing} to m:material choice of every sword"})
@Since("INSERT VERSION")
public class ExprRecipeIngredient extends SimpleExpression<Ingredient> {

    static {
        Skript.registerExpression(ExprRecipeIngredient.class, Ingredient.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
                "<[A-Za-z0-9]>\\:%recipechoice%");
    }

    private char key;
    private Expression<RecipeChoice> recipeChoice;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(SecShapedRecipe.ShapedRecipeCreateEvent.class)) {
            Skript.error("The 'recipe ingredient expression' is only usable within an advanced recipe section.");
            return false;
        }
        key = parseResult.regexes.get(0).group(0).charAt(0);
        recipeChoice = (Expression<RecipeChoice>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Ingredient[] get(Event event) {
        if (recipeChoice == null) return new Ingredient[0];
        return new Ingredient[]{new Ingredient(key, recipeChoice.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Ingredient> getReturnType() {
        return Ingredient.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return key + ":" + recipeChoice.toString(event, debug);
    }

}
