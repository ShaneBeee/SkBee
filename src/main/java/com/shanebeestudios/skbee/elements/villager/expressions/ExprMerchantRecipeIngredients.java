package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprMerchantRecipeIngredients extends SimpleExpression<ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprMerchantRecipeIngredients.class, ItemType.class,
                "(ingredients|1:result [item]) of merchant recipe %merchantrecipe%")
            .name("Merchant Recipe - Ingredients")
            .description("Represents the ingredients/result of a merchant recipe.",
                "Ingredients can be set, but the result can not be changed.",
                "If you wish to change the result, you will have to create a new merchant recipe.")
            .examples("set {_ing::*} to ingredients of merchant recipe {_recipe}",
                "set ingredients of merchant recipe {_recipe} to diamond and stone",
                "set {_result} to result item of merchant recipe {_recipe}")
            .since("1.17.0")
            .register();
    }

    private int pattern;
    private Expression<MerchantRecipe> recipe;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        this.recipe = (Expression<MerchantRecipe>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable ItemType[] get(Event event) {
        MerchantRecipe recipe = this.recipe.getSingle(event);
        if (recipe == null) return null;

        if (pattern == 0) {
            List<ItemType> items = new ArrayList<>();
            recipe.getIngredients().forEach(itemStack -> items.add(new ItemType(itemStack)));
            return items.toArray(new ItemType[0]);
        } else {
            ItemStack result = recipe.getResult();
            ItemType itemType = new ItemType(result);
            return new ItemType[]{itemType};
        }
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET && pattern == 0) {
            return CollectionUtils.array(ItemType[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        MerchantRecipe recipe = this.recipe.getSingle(event);
        if (recipe == null || mode != ChangeMode.SET) return;

        List<ItemStack> stacks = new ArrayList<>();
        for (Object object : delta) {
            if (object instanceof ItemType itemType) {
                if (stacks.size() <= 1) { // Recipe only accepts 2 ingredients
                    stacks.add(itemType.getRandom());
                }
            }
        }
        recipe.setIngredients(stacks);
    }

    @Override
    public boolean isSingle() {
        return this.pattern == 1;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "ingredients of merchant recipe " + this.recipe.toString(e, d);
    }

}
