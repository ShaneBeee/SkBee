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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Entity Damage Entity")
@Description({"Make an entity damage another entity by a given amount.",
        "Optional fake damage cause which you can retrieve with Skript's `last damage cause` expression."})
@Examples({"make last spawned entity damage player by 10",
        "make target entity damage player by 1 with fake damage cause poison"})
@Since("2.8.0, 2.18.0 (damage cause)")
public class EffEntityDamageEntity extends Effect {

    static {
        Skript.registerEffect(EffEntityDamageEntity.class,
                "make %entity% damage %livingentities% by %number% [with fake [damage] cause %-damagecause%]");
    }

    private Expression<LivingEntity> victims;
    private Expression<Entity> attacker;
    private Expression<Number> damageAmount;
    private Expression<DamageCause> damageCause;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.attacker = (Expression<Entity>) exprs[0];
        this.victims = (Expression<LivingEntity>) exprs[1];
        this.damageAmount = (Expression<Number>) exprs[2];
        this.damageCause = (Expression<DamageCause>) exprs[3];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        Number damageAmountNum = this.damageAmount.getSingle(event);
        double damageAmount = damageAmountNum != null ? damageAmountNum.doubleValue() : 0.0;

        Entity attacker = this.attacker.getSingle(event);
        if (attacker == null) return;

        DamageCause damageCause = null;
        if (this.damageCause != null) {
            damageCause = this.damageCause.getSingle(event);
        }
        for (LivingEntity victim : this.victims.getArray(event)) {
            if (damageCause != null) {
                victim.setLastDamageCause(new EntityDamageEvent(attacker, damageCause, damageAmount));
            }
            victim.damage(damageAmount, attacker);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String attacker = this.attacker.toString(e,d);
        String victim = this.victims.toString(e,d);
        String amount = this.damageAmount.toString(e,d);
        String fake = this.damageCause != null ? ("with fake damage cause " + this.damageCause.toString(e,d)) : "";
        return String.format("make %s damage %s by %s %s", attacker, victim, amount, fake);
    }

}
