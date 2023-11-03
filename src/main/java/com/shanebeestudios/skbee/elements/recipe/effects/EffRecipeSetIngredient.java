package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.recipe.ShapedRecipeCreateEvent;
import com.shanebeestudios.skbee.api.event.recipe.ShapelessRecipeCreateEvent;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Recipe - Ingredients")
@Description({"Set/add the ingredients of a shaped/shapeless recipe.",
        "This is specifically used in the ingredients section of shaped/shapeless recipe section.",
        "\n`set ingredient` is used for shaped recipes.",
        "\n`add ingredient` is used for shapeless recipes."})
@Examples("see shaped/shapeless recipe sections.")
@Since("3.0.0")
public class EffRecipeSetIngredient extends Effect {

    static {
        Skript.registerEffect(EffRecipeSetIngredient.class,
                "set ingredient (of|for) %string% to %itemstack/recipechoice%",
                "add %itemstack/recipechoice% to ingredients");
    }

    private int pattern;
    private Expression<String> key;
    private Expression<?> ingredient;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;

        if (this.pattern == 0 && !ParserInstance.get().isCurrentEvent(ShapedRecipeCreateEvent.class)) {
            Skript.error("`set ingredient` effect can only be used in an `ingredients` section of a shaped recipe section.");
            return false;
        } else if (this.pattern == 1 && !ParserInstance.get().isCurrentEvent(ShapelessRecipeCreateEvent.class)) {
            Skript.error("`add ingredients` effect can only be used in an `ingredients` section of a shapeless recipe section.");
            return false;
        }
        this.key = this.pattern == 0 ? (Expression<String>) exprs[0] : null;
        this.ingredient = exprs[this.pattern == 0 ? 1 : 0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Object ingredient = this.ingredient.getSingle(event);

        RecipeChoice recipeChoice = getRecipeChoice(ingredient);
        if (recipeChoice == null) return;

        if (this.pattern == 0 && event instanceof ShapedRecipeCreateEvent shapedEvent) {
            String key = this.key.getSingle(event);
            if (key == null || key.length() != 1) return;

            ShapedRecipe shapedRecipe = shapedEvent.getRecipe();
            if (Arrays.toString(shapedRecipe.getShape()).contains(key)) {
                char charKey = key.charAt(0);
                shapedRecipe.setIngredient(charKey, recipeChoice);

            }
        } else if (this.pattern == 1 && event instanceof ShapelessRecipeCreateEvent shapelessEvent) {
            ShapelessRecipe shapelessRecipe = shapelessEvent.getRecipe();
            if (shapelessRecipe.getChoiceList().size() < 9) shapelessRecipe.addIngredient(recipeChoice);
        }
    }

    @Nullable
    private static RecipeChoice getRecipeChoice(Object ingredient) {
        RecipeChoice recipeChoice = null;
        if (ingredient instanceof ItemStack itemStack) {
            Material material = itemStack.getType();
            if (itemStack.isSimilar(new ItemStack(material))) {
                recipeChoice = new RecipeChoice.MaterialChoice(material);
            } else {
                recipeChoice = new RecipeChoice.ExactChoice(itemStack);
            }
        } else if (ingredient instanceof RecipeChoice choice) {
            recipeChoice = choice;
        }
        return recipeChoice;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.pattern == 0) {
            return "set ingredient of " + this.key.toString(e, d) + " to " + this.ingredient.toString(e, d);
        } else {
            return "add " + this.ingredient.toString(e, d) + " to ingredients";
        }
    }

}
