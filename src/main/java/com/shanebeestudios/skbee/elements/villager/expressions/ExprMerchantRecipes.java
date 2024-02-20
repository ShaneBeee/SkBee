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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Merchant - Recipes")
@Description({"Represents the recipes of a merchant. All recipes returns a list of all the recipes this merchant offers.",
        "You can also set/delete them or add to them. You can also get/set/delete a specific recipe."})
@Examples({"add {_recipe} to merchant recipes of {_merchant}",
        "set merchant recipe 1 of {_merchant} to {_recipe}"})
@Since("1.17.0")
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

    @SuppressWarnings({"NullableProblems", "PatternVariableHidesField"})
    @Override
    protected MerchantRecipe @Nullable [] get(Event event) {
        if (this.merchant.getSingle(event) instanceof Merchant merchant) {
            if (this.all) {
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
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || (mode == ChangeMode.ADD && all)) {
            return CollectionUtils.array(MerchantRecipe[].class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions", "PatternVariableHidesField"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (this.merchant.getSingle(event) instanceof Merchant merchant) {
            if (this.all) {
                List<MerchantRecipe> newRecipes = new ArrayList<>();
                if (delta != null) {
                    for (Object object : delta) {
                        if (object instanceof MerchantRecipe merchantRecipe) {
                            if (!merchantRecipe.getIngredients().isEmpty()) newRecipes.add(merchantRecipe);
                        }
                    }
                }
                if (mode == ChangeMode.SET) {
                    merchant.setRecipes(newRecipes);
                } else if (mode == ChangeMode.DELETE) {
                    merchant.setRecipes(new ArrayList<>());
                } else if (mode == ChangeMode.ADD) {
                    List<MerchantRecipe> merchantRecipes = new ArrayList<>(merchant.getRecipes());
                    merchantRecipes.addAll(newRecipes);
                    merchant.setRecipes(merchantRecipes);
                }
            } else if (delta[0] instanceof MerchantRecipe merchantRecipe) {
                if (merchantRecipe.getIngredients().isEmpty()) return;
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
        return !this.all;
    }

    @Override
    public @NotNull Class<? extends MerchantRecipe> getReturnType() {
        return MerchantRecipe.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.all) {
            return "all merchant recipes of " + this.merchant.toString(e, d);
        }
        return "merchant recipe " + this.recipe.toString(e, d) + " of " + this.merchant.toString(e, d);
    }

}
