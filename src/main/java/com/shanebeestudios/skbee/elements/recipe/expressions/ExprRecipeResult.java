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
import com.shanebeestudios.skbee.api.recipe.RecipeUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Result")
@Description({"Get the result item of a recipe.",
        "\nID = Minecraft or custom NamespacedKey, see examples."})
@Examples({"set {_result} to result of recipe \"minecraft:oak_door\"",
        "set {_result} to result of recipe \"skbee:some_recipe\"",
        "set {_result} to result of recipe \"my_recipes:some_custom_recipe\""})
@Since("2.6.0")
public class ExprRecipeResult extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprRecipeResult.class, ItemType.class, ExpressionType.PROPERTY,
                "result[s] of recipe[s] [with id[s]] %strings%");
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
    protected @Nullable ItemType[] get(Event event) {
        List<ItemType> items = new ArrayList<>();
        for (String key : this.key.getArray(event)) {
            NamespacedKey namespacedKey = Util.getNamespacedKey(key, false);
            if (namespacedKey == null) continue;

            Recipe recipe = Bukkit.getRecipe(namespacedKey);
            if (recipe == null) continue;

            ItemStack result = recipe.getResult();
            items.add(new ItemType(result));
        }
        return items.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return this.key.isSingle();
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "result[s] of recipe[s] " + this.key.toString(e, d);
    }

}
