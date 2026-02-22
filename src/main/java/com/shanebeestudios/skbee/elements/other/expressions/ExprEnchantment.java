package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ExprEnchantment extends SimplePropertyExpression<EnchantmentType, Enchantment> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEnchantment.class, Enchantment.class, "enchantment", "enchantmenttypes")
            .name("Enchantment")
            .description("Get the type of enchantment from an Enchantment Type.")
            .examples("loop enchantments of player's tool:",
                "\tset {_level} to enchantment level of loop-value",
                "\tset {_enchant} to enchantment of loop-value")
            .since("1.16.0")
            .register();
    }

    @Override
    public @Nullable Enchantment convert(EnchantmentType enchantmentType) {
        return enchantmentType.getType();
    }

    @Override
    public @NotNull Class<? extends Enchantment> getReturnType() {
        return Enchantment.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "enchantment";
    }

}
