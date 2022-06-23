package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Merchant - Recipes")
@Description({"Represents the recipes of a merchant. All recipes returns a list of all the recipes this merchant offers.",
        "You can also set/delete them or add to them. You can also get/set/delete a specific recipe."})
@Examples({"add {_recipe} to merchant recipes of {_merchant}",
        "set merchant recipe 1 of {_merchant} to {_recipe}"})
@Since("INSERT VERSION")
public class ExprMerchantRecipes extends SimpleExpression<MerchantRecipe> {

    static {
        Skript.registerExpression(ExprMerchantRecipes.class, MerchantRecipe.class, ExpressionType.COMBINED,
                "[all] merchant recipes of %merchant/entity%",
                "merchant recipe %number% of %merchant/entity%");
    }

    private Expression<Object> merchant;
    private Expression<Number> recipe;
    private boolean all;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.merchant = (Expression<Object>) exprs[i];
        this.recipe = i == 1 ? (Expression<Number>) exprs[0] : null;
        this.all = i == 0;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable MerchantRecipe[] get(Event event) {
        if (this.merchant.getSingle(event) instanceof Merchant merchant) {
            if (all) {
                return merchant.getRecipes().toArray(new MerchantRecipe[0]);
            } else {
                int recipe = 0;
                if (this.recipe != null) {
                    Number number = this.recipe.getSingle(event);
                    if (number != null) recipe = number.intValue();
                }
                int count = merchant.getRecipeCount();
                if (count == 0) return null;

                recipe -= 1;
                if (recipe < 0) recipe = 0;
                if (recipe > (count - 1)) return null;

                return new MerchantRecipe[]{merchant.getRecipe(recipe)};
            }
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || (mode == ChangeMode.ADD && all)) {
            return CollectionUtils.array(MerchantRecipe[].class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (this.merchant.getSingle(event) instanceof Merchant merchant) {
            if (all) {
                MerchantRecipe[] recipes = delta != null ? ((MerchantRecipe[]) delta) : null;
                if (mode == ChangeMode.SET) {
                    List<MerchantRecipe> recipesList = new ArrayList<>(Arrays.asList(recipes));
                    merchant.setRecipes(recipesList);
                } else if (mode == ChangeMode.DELETE) {
                    merchant.setRecipes(new ArrayList<>());
                } else if (mode == ChangeMode.ADD) {
                    List<MerchantRecipe> merchantRecipes = new ArrayList<>(merchant.getRecipes());
                    merchantRecipes.addAll(Arrays.asList(recipes));
                    merchant.setRecipes(merchantRecipes);
                }
            } else if (delta[0] instanceof MerchantRecipe merchantRecipe) {
                int recipe = this.recipe.getSingle(event).intValue() - 1;
                if (recipe < 0) recipe = 0;
                if (recipe >= merchant.getRecipeCount()) {
                    List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
                    recipes.add(merchantRecipe);
                    merchant.setRecipes(recipes);
                } else {
                    merchant.setRecipe(recipe, merchantRecipe);
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return !all;
    }

    @Override
    public @NotNull Class<? extends MerchantRecipe> getReturnType() {
        return MerchantRecipe.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (all) {
            return "all merchant recipes of " + merchant.toString(e, d);
        }
        return "merchant recipe " + recipe.toString(e, d) + " of " + merchant.toString(e, d);
    }

}
