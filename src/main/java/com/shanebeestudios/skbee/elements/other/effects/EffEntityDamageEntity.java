package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffEntityDamageEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffEntityDamageEntity.class,
                "make %entity% damage %livingentities% by %number%")
            .name("Entity Damage Entity")
            .description("Make an entity damage another entity by a given amount.")
            .examples("make last spawned entity damage player by 10",
                "make target entity damage player by 1")
            .since("2.8.0")
            .register();
    }

    private Expression<LivingEntity> victims;
    private Expression<Entity> attacker;
    private Expression<Number> damageAmount;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.attacker = (Expression<Entity>) exprs[0];
        this.victims = (Expression<LivingEntity>) exprs[1];
        this.damageAmount = (Expression<Number>) exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number damageAmountNum = this.damageAmount.getSingle(event);
        double damageAmount = damageAmountNum != null ? damageAmountNum.doubleValue() : 0.0;

        Entity attacker = this.attacker.getSingle(event);
        if (attacker == null) {
            return;
        }

        for (LivingEntity victim : this.victims.getArray(event)) {
            victim.damage(damageAmount, attacker);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String attacker = this.attacker.toString(e, d);
        String victim = this.victims.toString(e, d);
        String amount = this.damageAmount.toString(e, d);
        return String.format("make %s damage %s by %s", attacker, victim, amount);
    }

}
