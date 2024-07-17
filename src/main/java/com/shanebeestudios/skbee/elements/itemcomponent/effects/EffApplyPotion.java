package com.shanebeestudios.skbee.elements.itemcomponent.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.elements.itemcomponent.sections.SecFoodComponent.FoodComponentApplyEvent;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

@Name("Component - Food Component Apply Potion Effect")
@Description({"Apply a potion effect to a food component. This works in the `effects` section of a food component section.",
    "Probability is an optional value between 0 and 100. This is the chance the player will get this effect when eaten."})
@Examples({"apply food component to player's tool:",
    "\tnutrition: 5",
    "\tsaturation: 3",
    "\tusing converts to: 1 of bowl",
    "\tcan always eat: true",
    "\teffects:",
    "\t\tapply potion effect of nausea without particles for 10 seconds",
    "\t\tapply potion effect of poison without particles for 5 seconds with probability 50"})
public class EffApplyPotion extends Effect {

    static {
        Skript.registerEffect(EffApplyPotion.class, "apply [potion[[ ]effect]] %potioneffect% [with probability %-number%]");
    }

    private Expression<PotionEffect> effect;
    private Expression<Number> probability;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(FoodComponentApplyEvent.class)) {
            Skript.error("Potion effect can only be applied in the 'effects' section of a food component section.");
            return false;
        }
        this.effect = (Expression<PotionEffect>) expr[0];
        this.probability = (Expression<Number>) expr[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (event instanceof FoodComponentApplyEvent applyEvent) {
            PotionEffect effect = this.effect.getSingle(event);
            if (effect == null) return;

            Number probNum = this.probability != null ? this.probability.getSingle(event) : null;
            float probability = probNum != null ? MathUtil.clamp((probNum.floatValue() / 100), 0.0F, 1.0F) : 1;

            applyEvent.getComponent().addEffect(effect, probability);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "apply " + this.effect.toString(e, d) + (this.probability != null ? (" with probability " + this.probability.toString(e, d)) : "");
    }

}
