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
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - CookTime")
@Description("Get the cooktime of a cooking recipes.")
@Examples("set {_time} to cooktime of recipe with id \"minecraft:cooked_chicken\"")
@Since("2.18.0")
public class ExprRecipeCookTime extends SimpleExpression<Timespan> {

    static {
        Skript.registerExpression(ExprRecipeCookTime.class, Timespan.class, ExpressionType.COMBINED,
                "cook[ ]time of recipe[s] [with id[s]] %strings%");
    }

    private Expression<String> key;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Timespan[] get(Event event) {
        List<Timespan> cookTimes = new ArrayList<>();
        for (String key : this.key.getArray(event)) {
            NamespacedKey namespacedKey = Util.getNamespacedKey(key, false);
            if (namespacedKey == null) continue;

            Recipe recipe = Bukkit.getRecipe(namespacedKey);
            if (recipe instanceof CookingRecipe<?> cookingRecipe)
                cookTimes.add(Timespan.fromTicks(cookingRecipe.getCookingTime()));

        }
        return cookTimes.toArray(new Timespan[0]);
    }

    @Override
    public boolean isSingle() {
        return this.key.isSingle();
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "cook time of recipe " + this.key.toString(e, d);
    }

}
