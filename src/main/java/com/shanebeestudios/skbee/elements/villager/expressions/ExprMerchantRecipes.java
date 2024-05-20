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
        "You can also set/delete them or add to them. You can also get/set/delete a specific recipe.",
        "<b>note: when setting/deleting a specific merchant recipe the merchant MUST have a recipe in that slot</b>"})
@Examples({"add {_recipe} to merchant recipes of {_merchant}",
        "set merchant recipe 1 of {_merchant} to {_recipe}"})
@Since("1.17.0")
public class ExprMerchantRecipes extends SimpleExpression<MerchantRecipe> {

    static {
        Skript.registerExpression(ExprMerchantRecipes.class, MerchantRecipe.class, ExpressionType.COMBINED,
                "[all] merchant recipes of %merchants/entities%",
                "merchant recipe %number% of %merchants/entities%");
    }

    private Expression<?> merchants;
    private Expression<Number> recipe;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.merchants = exprs[matchedPattern];
        this.recipe = matchedPattern == 1 ? (Expression<Number>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings({"NullableProblems", "PatternVariableHidesField"})
    @Override
    protected MerchantRecipe @Nullable [] get(Event event) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        if (this.recipe != null) {
            Number number = this.recipe.getSingle(event);
            if (number == null) return null;
            int recipe = number.intValue() - 1; // 1 = 0, 0 = -1
            if (recipe < 0) return null;
            for (Object obj : this.merchants.getArray(event)) {
                if (!(obj instanceof Merchant merchant)) continue;
                int recipeCount = merchant.getRecipeCount();
                if (recipeCount == 0 || recipe > recipeCount - 1) continue;
                recipes.add(merchant.getRecipe(recipe));
            }
        } else {
            for (Object obj : this.merchants.getArray(event)) {
                if (!(obj instanceof Merchant merchant)) continue;
                recipes.addAll(merchant.getRecipes());
            }
        }
        return recipes.toArray(MerchantRecipe[]::new);
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (this.recipe == null) {
            return switch (mode) {
                case ADD, SET, DELETE -> CollectionUtils.array(MerchantRecipe[].class);
                default -> null;
            };
        }
        switch (mode) {
            case SET, DELETE:
                return CollectionUtils.array(MerchantRecipe.class);
            case ADD:
                Skript.error("You can only add a merchant recipe if you're using the 'all merchant recipes' syntax.");
            default:
                return null;
        }
    }

    @Override
    public void change(Event event, Object@Nullable [] delta, ChangeMode mode) {
        if (this.recipe == null) {
            List<MerchantRecipe> recipes = new ArrayList<>();
            if (delta != null) {
                for (Object object : delta) {
                    if (!(object instanceof MerchantRecipe merchantRecipe)) continue;
                    if (merchantRecipe.getIngredients().isEmpty()) continue;
                    recipes.add(merchantRecipe);
                }
            }
            for (Object obj : this.merchants.getArray(event)) {
                if (!(obj instanceof Merchant merchant)) continue;
                // create a copy of the recipes to update/modify existing recipes
                List<MerchantRecipe> recipeCopy = new ArrayList<>(recipes);
                switch (mode) {
                    case ADD:
                        recipeCopy.addAll(0, merchant.getRecipes());
                    case DELETE, SET:
                        merchant.setRecipes(recipeCopy);
                        break;
                }
            }
            return;
        }
        if (mode == ChangeMode.ADD) return;

        Number num = this.recipe.getSingle(event);
        if (num == null) return;
        int recipeSlot = num.intValue() - 1; // 1 = 0 | 0 = -1
        if (recipeSlot < 0) return;
        MerchantRecipe merchantRecipe = delta != null ? (MerchantRecipe) delta[0] : null;
        if (merchantRecipe != null && merchantRecipe.getIngredients().isEmpty()) return;
        for (Object obj : this.merchants.getArray(event)) {
            if (!(obj instanceof Merchant merchant)) continue;
            int recipeCount = merchant.getRecipeCount();
            if (recipeCount == 0 || recipeCount - 1 < recipeSlot) continue;
            switch (mode) {
                case SET:
                    assert merchantRecipe != null;
                    merchant.setRecipe(recipeSlot, merchantRecipe);
                    break;
                case DELETE:
                    List<MerchantRecipe> updatedRecipes = new ArrayList<>(merchant.getRecipes());
                    if (updatedRecipes.size() >= recipeSlot) {
                        updatedRecipes.remove(recipeSlot);
                        merchant.setRecipes(updatedRecipes);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.recipe != null && this.merchants.isSingle();
    }

    @Override
    public @NotNull Class<? extends MerchantRecipe> getReturnType() {
        return MerchantRecipe.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.recipe == null) {
            return "all merchant recipes of " + this.merchants.toString(e, d);
        }
        return "merchant recipe " + this.recipe.toString(e, d) + " of " + this.merchants.toString(e, d);
    }

}
