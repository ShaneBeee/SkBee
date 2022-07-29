package com.shanebeestudios.skbee.elements.recipe.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems"})
@Name("Recipe - Ingredients of Recipe")
@Description("Get the ingredients from a recipe. Requires 1.13+")
@Examples({"set {_ing::*} to ingredients of recipe \"minecraft:diamond_sword\"",
        "loop recipes for iron ingot:",
        "\tset {_ing::*} to ingredients of recipe %loop-value%"})
@Since("1.4.0")
public class ExprIngredientsOfRecipe extends SimpleExpression<ItemType> {

    private static final boolean HAS_COOKING = Skript.classExists("org.bukkit.inventory.CookingRecipe");
    private static final boolean HAS_STONECUTTING = Skript.classExists("org.bukkit.inventory.StonecuttingRecipe");

    static {
        if (Skript.classExists("org.bukkit.Keyed")) {
            Skript.registerExpression(ExprIngredientsOfRecipe.class, ItemType.class, ExpressionType.COMBINED,
                    "[(all [[of] the]|the)] ingredients (for|of) recipe %string%");
        }
    }

    private Expression<String> recipe;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        recipe = (Expression<String>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected ItemType[] get(Event e) {
        List<ItemType> items = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof Keyed keyed && keyed.getKey().toString().equalsIgnoreCase(this.recipe.getSingle(e))) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    String[] shape = shapedRecipe.getShape();

                    for (int i = 0; i < 9; i++) {
                        items.add(new ItemType(Material.AIR));
                    }

                    for (int i = 0; i < shape.length; i++) {

                        for (int x = 0; x < shape[i].length(); x++) {


                            ItemStack ingredient = shapedRecipe.getIngredientMap().get(shape[i].toCharArray()[x]);
                            items.set(i * 3 + x + 1, new ItemType(ingredient));

                        }

                    }
                } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    for (ItemStack ingredient : shapelessRecipe.getIngredientList()) {
                        if (ingredient == null) continue;
                        items.add(new ItemType(ingredient));
                    }
                } else if (HAS_COOKING && recipe instanceof CookingRecipe cookingRecipe) {
                    items.add(new ItemType(cookingRecipe.getInput()));
                } else if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                    items.add(new ItemType(furnaceRecipe.getInput()));
                } else if (recipe instanceof MerchantRecipe merchantRecipe) {
                    for (ItemStack ingredient : merchantRecipe.getIngredients()) {
                        if (ingredient == null) continue;
                        items.add(new ItemType(ingredient));
                    }
                } else if (HAS_STONECUTTING && recipe instanceof StonecuttingRecipe stonecuttingRecipe) {
                    items.add(new ItemType(stonecuttingRecipe.getInput()));
                }
            }
        });

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
        return "ingredients of recipe " + recipe.toString(e, d);
    }
}
