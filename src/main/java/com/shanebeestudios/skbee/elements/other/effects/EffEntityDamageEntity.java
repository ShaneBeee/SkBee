package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Entity Damage Entity")
@Description("Make an entity damage another entity by a given amount.")
@Examples("make last spawned entity damage player by 10")
@Since("2.8.0")
public class EffEntityDamageEntity extends Effect {

    static {
        Skript.registerEffect(EffEntityDamageEntity.class,
                "make %entity% damage %livingentities% by %number%");
    }

    private Expression<LivingEntity> victims;
    private Expression<Entity> attacker;
    private Expression<Number> damageAmount;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.attacker = (Expression<Entity>) exprs[0];
        this.victims = (Expression<LivingEntity>) exprs[1];
        this.damageAmount = (Expression<Number>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Number damageAmountNum = this.damageAmount.getSingle(event);
        double damageAmount = damageAmountNum != null ? damageAmountNum.doubleValue() : 0.0;

        Entity attacker = this.attacker.getSingle(event);
        if (attacker == null) return;

        for (LivingEntity victim : this.victims.getArray(event)) {
            victim.damage(damageAmount, attacker);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "make " + this.attacker.toString(e, d) + " damage " +
                this.victims.toString(e, d) + " by " + this.damageAmount.toString(e, d);
    }

}
