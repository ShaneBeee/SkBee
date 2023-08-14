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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Charging Attack")
@Description({"Represents a RangedEntity that is \"charging\" up an attack, by raising its hands.",
        "\nRangedEntities: Drowned, Illusioner, Llama, Piglin, Pillager, Skeleton, Snowman, Stray, TraderLlama, Witch, Wither, WitherSkeleton."})
@Examples("set charging attack of last spawned entity to true")
@Since("INSERT VERSION")
public class ExprChargingAttack extends SimplePropertyExpression<Entity, Boolean> {

    static {
        if (Skript.classExists("com.destroystokyo.paper.entity.RangedEntity")) {
            register(ExprChargingAttack.class, Boolean.class, "charging attack", "livingentities");
        }
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof RangedEntity rangedEntity) return rangedEntity.isChargingAttack();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean bool) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof RangedEntity rangedEntity) rangedEntity.setChargingAttack(bool);
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
