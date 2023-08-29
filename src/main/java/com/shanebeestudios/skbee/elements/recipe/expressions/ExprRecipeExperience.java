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
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Experience")
@Description("Get the experience gained from a cooking recipe.")
@Examples("set {_time} to experience of recipe with id \"minecraft:cooked_chicken\"")
@Since("INSERT VERSION")
public class ExprRecipeExperience extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprRecipeExperience.class, Number.class, ExpressionType.COMBINED,
                "(experience|[e]xp) of recipe[s] [with id[s]] %strings%");
    }

    private Expression<String> key;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.key = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Number[] get(Event event) {
        List<Number> exp = new ArrayList<>();
        for (String key : this.key.getArray(event)) {
            NamespacedKey namespacedKey = RecipeUtil.getKey(key);
            if (namespacedKey == null) continue;

            Recipe recipe = Bukkit.getRecipe(namespacedKey);
            if (recipe instanceof CookingRecipe<?> cookingRecipe)
                exp.add(cookingRecipe.getExperience());

        }
        return exp.toArray(new Number[0]);
    }

    @Override
    public boolean isSingle() {
        return this.key.isSingle();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "experience of recipe " + this.key.toString(e, d);
    }

}
