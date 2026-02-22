package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprIngredientsOfRecipe extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprIngredientsOfRecipe.class, ItemType.class,
                "[(all [[of] the]|the)] ingredients (for|of) recipe %string%")
            .name("Recipe - Ingredients of Recipe")
            .description("Get the ingredients from a recipe.")
            .examples("set {_ing::*} to ingredients of recipe \"minecraft:diamond_sword\"",
                "loop recipes for iron ingot:",
                "\tset {_ing::*} to ingredients of recipe %loop-value%")
            .since("1.4.0")
            .register();
    }

    private Expression<String> recipe;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.recipe = (Expression<String>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected ItemType[] get(Event event) {
        String recipeSingle = this.recipe.getSingle(event);
        if (recipeSingle == null) return null;

        NamespacedKey namespacedKey = Util.getNamespacedKey(recipeSingle, false);
        if (namespacedKey == null) return null;

        List<ItemType> items = new ArrayList<>();
        Recipe recipe = Bukkit.getRecipe(namespacedKey);
        if (recipe instanceof Keyed keyed && keyed.getKey().equals(namespacedKey)) {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                String[] shape = shapedRecipe.getShape();
                int length = Math.max(shape.length, shape[0].length());
                for (int i = 0; i < Math.pow(length, 2); i++) {
                    items.add(new ItemType(Material.AIR));
                }
                for (int i = 0; i < shape.length; i++) {
                    for (int x = 0; x < shape[i].length(); x++) {
                        ItemStack ingredient = shapedRecipe.getIngredientMap().get(shape[i].toCharArray()[x]);
                        if (ingredient != null) items.set(i * length + x, new ItemType(ingredient));
                    }
                }
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                for (ItemStack ingredient : shapelessRecipe.getIngredientList()) {
                    if (ingredient == null) continue;
                    items.add(new ItemType(ingredient));
                }
            } else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                items.add(new ItemType(cookingRecipe.getInput()));
            } else if (recipe instanceof MerchantRecipe merchantRecipe) {
                for (ItemStack ingredient : merchantRecipe.getIngredients()) {
                    if (ingredient == null) continue;
                    items.add(new ItemType(ingredient));
                }
            } else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                items.add(new ItemType(stonecuttingRecipe.getInput()));
            } else if (recipe instanceof SmithingRecipe smithingRecipe) {
                items.add(new ItemType(smithingRecipe.getBase().getItemStack()));
                items.add(new ItemType(smithingRecipe.getAddition().getItemStack()));
            }
        }

        return items.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "ingredients of recipe " + this.recipe.toString(e, d);
    }

}
