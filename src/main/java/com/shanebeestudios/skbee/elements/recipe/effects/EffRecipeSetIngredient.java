package com.shanebeestudios.skbee.elements.recipe.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.event.recipe.ShapedRecipeCreateEvent;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Recipe - Ingredients")
@Since("INSERT VERSION")
public class EffRecipeSetIngredient extends Effect {

    static {
        Skript.registerEffect(EffRecipeSetIngredient.class,
                "set ingredient of %string% to %itemtype/materialchoice%");
    }

    private Expression<String> key;
    private Expression<?> ingredient;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(ShapedRecipeCreateEvent.class)) {
            Skript.error("`set ingredient` effect can only be used in an `ingredients` section of a shaped recipe section.");
        }
        this.key = (Expression<String>) exprs[0];
        this.ingredient = exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (!(event instanceof ShapedRecipeCreateEvent recipeEvent)) return;

        String key = this.key.getSingle(event);
        if (key == null || key.length() != 1) return;

        ShapedRecipe shapedRecipe = recipeEvent.getRecipe();
        if (Arrays.toString(shapedRecipe.getShape()).contains(key)) {
            char charKey = key.charAt(0);
            Object ingredient = this.ingredient.getSingle(event);

            if (ingredient instanceof ItemType itemType) {
                ItemStack itemStack = itemType.getRandom();
                Material material = itemStack.getType();
                if (itemStack.equals(new ItemStack(material))) {
                    shapedRecipe.setIngredient(charKey, material);
                } else {
                    shapedRecipe.setIngredient(charKey, new RecipeChoice.ExactChoice(itemStack));
                }
            } else if (ingredient instanceof RecipeChoice recipeChoice) {
                shapedRecipe.setIngredient(charKey, recipeChoice);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "set ingredient of " + this.key.toString(e, d) + " to " + this.ingredient.toString(e, d);
    }

}
