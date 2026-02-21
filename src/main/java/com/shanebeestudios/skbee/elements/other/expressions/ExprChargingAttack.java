package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.entity.RangedEntity;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChargingAttack extends SimplePropertyExpression<Entity, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprChargingAttack.class, Boolean.class, "charging attack", "livingentities")
            .name("Charging Attack")
            .description("Represents a RangedEntity that is \"charging\" up an attack, by raising its hands.",
                "Setting requires a PaperMC server.",
                "RangedEntities: Drowned, Illusioner, Llama, Piglin, Pillager, Skeleton, Snowman, Stray, TraderLlama, Witch, Wither, WitherSkeleton.")
            .examples("set charging attack of last spawned entity to true",
                "set {_charge} to charging attack of target entity of player",
                "if charging attack of target entity of player = true:")
            .since("2.17.0")
            .register();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof RangedEntity rangedEntity) return rangedEntity.isChargingAttack();
        else if (entity instanceof LivingEntity livingEntity) return livingEntity.isHandRaised();
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean bool) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Mob mob) mob.setAggressive(bool);
                else if (entity instanceof RangedEntity rangedEntity)
                    rangedEntity.setChargingAttack(bool);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "charging attack";
    }

}
