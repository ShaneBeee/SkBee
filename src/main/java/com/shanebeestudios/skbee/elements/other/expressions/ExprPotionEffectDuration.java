package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Potion Effect Duration")
@Description({"Get the duration of a potion effect. If running 1.19.4+ and the potion is infinite,",
    "it will return as the max available time."})
@Examples("set {_duration::*} to potion duration of active potion effects of player")
@Since("2.8.5")
public class ExprPotionEffectDuration extends SimplePropertyExpression<PotionEffect, Timespan> {

    static {
        register(ExprPotionEffectDuration.class, Timespan.class, "potion [effect] duration[s]", "potioneffects");
    }

    @Override
    public @Nullable Timespan convert(PotionEffect potionEffect) {
        int duration = potionEffect.getDuration();
        // Duration will be -1 when infinite
        duration = duration < 0 ? Integer.MAX_VALUE : duration;
        return new Timespan(Timespan.TimePeriod.TICK, duration);
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "potion effect duration";
    }

}
