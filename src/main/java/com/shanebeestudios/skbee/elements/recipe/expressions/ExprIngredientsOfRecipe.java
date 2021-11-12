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
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
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
        Bukkit.recipeIterator().forEachRemaining(r -> {
            if (r instanceof Keyed && ((Keyed) r).getKey().toString().equalsIgnoreCase(this.recipe.getSingle(e))) {
                if (r instanceof ShapedRecipe) {
                    for (ItemStack ingredient : ((ShapedRecipe) r).getIngredientMap().values()) {
                        if (ingredient == null) continue;
                        items.add(new ItemType(ingredient));
                    }
                } else if (r instanceof ShapelessRecipe) {
                    for (ItemStack ingredient : ((ShapelessRecipe) r).getIngredientList()) {
                        if (ingredient == null) continue;
                        items.add(new ItemType(ingredient));
                    }
                } else if (HAS_COOKING && r instanceof CookingRecipe) {
                    items.add(new ItemType(((CookingRecipe<?>) r).getInput()));
                } else if (r instanceof FurnaceRecipe) {
                    items.add(new ItemType(((FurnaceRecipe) r).getInput()));
                } else if (r instanceof MerchantRecipe) {
                    for (ItemStack ingredient : ((MerchantRecipe) r).getIngredients()) {
                        if (ingredient == null) continue;
                        items.add(new ItemType(ingredient));
                    }
                } else if (HAS_STONECUTTING && r instanceof StonecuttingRecipe) {
                    items.add(new ItemType(((StonecuttingRecipe) r).getInput()));
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
