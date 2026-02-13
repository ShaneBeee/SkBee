package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ExprEnchantmentLevel extends SimplePropertyExpression<EnchantmentType, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEnchantmentLevel.class, Number.class, "enchantment level", "enchantmenttypes")
            .name("Enchantment Level")
            .description("Get the enchantment level from an Enchantment Type.")
            .examples("loop enchantments of player's tool:",
                "\tset {_level} to enchantment level of loop-value",
                "\tset {_enchant} to enchantment of loop-value")
            .since("1.16.0")
            .register();
    }

    @Override
    public @Nullable Number convert(EnchantmentType enchantmentType) {
        return enchantmentType.getLevel();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "enchantment level";
    }

}
