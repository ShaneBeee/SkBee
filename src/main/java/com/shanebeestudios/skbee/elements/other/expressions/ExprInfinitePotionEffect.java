package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Infinite Potion Effect")
@Description({"Represents an infinite potion effect. This is similar to Skript's potion effect expression except it is infinite time.",
        "This can be used in Skript's `apply %potion effects% to %living entities%` effect!",
        "\nNOTE: This will cause console errors when sending as a string (This is an issue in Skript itself).",
        "This is only a temp solution until Skript adds a proper solution."})
@Examples({"set {_potion} to infinite potion effect of night vision of tier 1 without particles",
        "apply {_potion} to player"})
@Since("2.8.5")
public class ExprInfinitePotionEffect extends SimpleExpression<PotionEffect> {

    static {
        if (Skript.isRunningMinecraft(1, 19, 4)) {
            Skript.registerExpression(ExprInfinitePotionEffect.class, PotionEffect.class, ExpressionType.COMBINED,
                    "[new] [:ambient] infinite potion effect[s] of %potioneffecttypes% [[[of] tier] %number%] [particles:without particles]");
        }
    }

    private boolean ambient;
    private boolean particles;
    private Expression<PotionEffectType> potionEffectTypes;
    private Expression<Number> tier;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.ambient = parseResult.hasTag("ambient");
        this.particles = !parseResult.hasTag("particles");
        this.potionEffectTypes = (Expression<PotionEffectType>) exprs[0];
        this.tier = (Expression<Number>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable PotionEffect[] get(Event event) {
        int tier = 0;
        if (this.tier != null) {
            Number tierNumber = this.tier.getSingle(event);
            if (tierNumber != null) {
                tier = tierNumber.intValue() - 1;
            }
        }
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (PotionEffectType type : this.potionEffectTypes.getArray(event)) {
            PotionEffect potionEffect = new PotionEffect(type, PotionEffect.INFINITE_DURATION, tier, this.ambient, this.particles);
            potionEffects.add(potionEffect);
        }
        return potionEffects.toArray(new PotionEffect[0]);
    }

    @Override
    public boolean isSingle() {
        return this.potionEffectTypes.isSingle();
    }

    @Override
    public @NotNull Class<? extends PotionEffect> getReturnType() {
        return PotionEffect.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String ambient = this.ambient ? "ambient " : "";
        String tier = this.tier != null ? " of tier " + this.tier.toString(e,d) : "";
        String particles = this.particles ? " without particles" : "";
        return ambient + "potion effect of " + this.potionEffectTypes.toString(e,d) + tier + particles;
    }

}
