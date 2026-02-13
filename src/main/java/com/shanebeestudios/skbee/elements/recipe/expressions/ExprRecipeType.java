package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.recipe.RecipeType;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprRecipeType extends SimpleExpression<RecipeType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprRecipeType.class, RecipeType.class,
                "recipe type of recipe[s] [with id[s]] %strings%")
            .name("Recipe - Recipe Type")
            .description("Get the type of a recipe.",
                "`ID` = Minecraft or custom NamespacedKey, see examples.")
            .examples("set {_type} to recipe type of recipe with id \"minecraft:oak_door\"",
                "set {_type} to recipe type of recipe \"skbee:some_recipe\"",
                "set {_type} to recipe type of recipe with id \"my_recipes:some_custom_recipe\"",
                "if recipe type of recipe with id \"my_recipes:some_custom_recipe\" = shaped recipe:")
            .since("2.6.0")
            .register();
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
    protected @Nullable RecipeType[] get(Event event) {
        List<RecipeType> types = new ArrayList<>();
        for (String key : this.key.getArray(event)) {
            NamespacedKey namespacedKey = Util.getNamespacedKey(key, false);
            if (namespacedKey == null) continue;

            Recipe recipe = Bukkit.getRecipe(namespacedKey);
            if (recipe == null) continue;

            RecipeType recipeType = RecipeType.getFromRecipe(recipe);
            if (recipeType != null) {
                types.add(recipeType);
            }
        }
        return types.toArray(new RecipeType[0]);
    }

    @Override
    public boolean isSingle() {
        return this.key.isSingle();
    }

    @Override
    public @NotNull Class<? extends RecipeType> getReturnType() {
        return RecipeType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "recipe type of recipe " + this.key.toString(e, d);
    }

}
