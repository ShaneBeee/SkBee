package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.EnchantmentType;
import org.bukkit.enchantments.Enchantment;
import org.eclipse.jdt.annotation.Nullable;

@Name("Enchantment")
@Description("Get the type of enchantment from an Enchantment Type.")
@Examples({"loop enchantments of player's tool:",
        "\tset {_level} to enchantment level of loop-value",
        "\tset {_enchant} to enchantment of loop-value"})
@Since("INSERT VERSION")
public class ExprEnchantment extends SimplePropertyExpression<EnchantmentType, Enchantment> {

    static {
        register(ExprEnchantment.class, Enchantment.class, "enchantment", "enchantmenttypes");
    }

    @Override
    public @Nullable Enchantment convert(EnchantmentType enchantmentType) {
        return enchantmentType.getType();
    }

    @Override
    public Class<? extends Enchantment> getReturnType() {
        return Enchantment.class;
    }

    @Override
    protected String getPropertyName() {
        return "enchantment";
    }

}
