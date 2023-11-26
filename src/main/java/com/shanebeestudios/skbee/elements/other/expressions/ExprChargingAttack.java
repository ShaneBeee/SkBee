package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.entity.RangedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Charging Attack")
@Description({"Represents a RangedEntity that is \"charging\" up an attack, by raising its hands.",
        "\nSetting requires a PaperMC server.",
        "\nRangedEntities: Drowned, Illusioner, Llama, Piglin, Pillager, Skeleton, Snowman, Stray, TraderLlama, Witch, Wither, WitherSkeleton."})
@Examples({"set charging attack of last spawned entity to true",
        "set {_charge} to charging attack of target entity of player",
        "if charging attack of target entity of player = true:"})
@Since("2.17.0")
public class ExprChargingAttack extends SimplePropertyExpression<Entity, Boolean> {

    private static final boolean HAS_RANGED = Skript.classExists("com.destroystokyo.paper.entity.RangedEntity");
    private static final boolean HAS_RAISED_HAND = Skript.methodExists(LivingEntity.class, "isHandRaised");
    private static final boolean HAS_AGGRESSIVE = Skript.methodExists(Mob.class, "isAggressive");

    static {
        if (HAS_RANGED || HAS_RAISED_HAND) {
            register(ExprChargingAttack.class, Boolean.class, "charging attack", "livingentities");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (HAS_RAISED_HAND && entity instanceof LivingEntity livingEntity) return livingEntity.isHandRaised();
        else if (HAS_RANGED && entity instanceof RangedEntity rangedEntity) return rangedEntity.isChargingAttack();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (!HAS_RANGED && !HAS_AGGRESSIVE) return null;
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean bool) {
            for (Entity entity : getExpr().getArray(event)) {
                if (HAS_AGGRESSIVE && entity instanceof Mob mob) mob.setAggressive(bool);
                else if (HAS_RANGED && entity instanceof RangedEntity rangedEntity) rangedEntity.setChargingAttack(bool);
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
