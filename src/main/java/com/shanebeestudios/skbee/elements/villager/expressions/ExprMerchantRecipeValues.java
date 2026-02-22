package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.Nullable;

public class ExprMerchantRecipeValues extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprMerchantRecipeValues.class, Number.class,
                "merchant recipe (0:demand|1:max uses|2:price multiplier|3:special price|4:uses|5:villager experience) of %merchantrecipe%")
            .name("Merchant Recipe - Values")
            .description("Represents different number values of a merchant recipe.")
            .examples("set {_uses} to merchant recipe uses of {_recipe}",
                "set merchant recipe demand of {_recipe} to 2")
            .since("3.12.0")
            .register();
    }

    private int pattern;
    private Expression<MerchantRecipe> recipe;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        this.recipe = (Expression<MerchantRecipe>) exprs[0];
        return true;
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        MerchantRecipe recipe = this.recipe.getSingle(event);
        if (recipe == null) return null;

        Number num = switch (this.pattern) {
            case 1 -> recipe.getMaxUses();
            case 2 -> recipe.getPriceMultiplier();
            case 3 -> recipe.getSpecialPrice();
            case 4 -> recipe.getUses();
            case 5 -> recipe.getVillagerExperience();
            default -> recipe.getDemand();
        };
        return new Number[]{num};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Number value && mode == ChangeMode.SET) {
            MerchantRecipe recipe = this.recipe.getSingle(event);
            if (recipe == null) return;

            switch (this.pattern) {
                case 1 -> recipe.setMaxUses(value.intValue());
                case 2 -> recipe.setPriceMultiplier(value.intValue());
                case 3 -> recipe.setSpecialPrice(value.intValue());
                case 4 -> recipe.setUses(value.intValue());
                case 5 -> recipe.setVillagerExperience(value.intValue());
                default -> recipe.setDemand(value.intValue());
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String type = switch (this.pattern) {
            case 1 -> "max uses";
            case 2 -> "price multiplier";
            case 3 -> "special price";
            case 4 -> "uses";
            case 5 -> "villager experience";
            default -> "demand";
        };
        return "merchant recipe " + type + " of " + this.recipe.toString(e, d);
    }

}
