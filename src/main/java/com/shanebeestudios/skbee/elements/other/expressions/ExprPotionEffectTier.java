package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Potion Effect Tier")
@Description("Get the tier of a potion effect.")
@Examples("set {_tiers::*} to potion tiers of active potion effects of player")
@Since("INSERT VERSION")
public class ExprPotionEffectTier extends SimplePropertyExpression<PotionEffect,Number> {

    static {
        register(ExprPotionEffectTier.class, Number.class, "potion [effect] (tier|amplifier)[s]", "potioneffects");
    }

    @Override
    public @Nullable Number convert(PotionEffect potionEffect) {
        return potionEffect.getAmplifier();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "potion effect tier";
    }

}
