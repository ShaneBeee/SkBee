package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMobAwareness extends SimplePropertyExpression<Entity, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprMobAwareness.class, Boolean.class, "mob awareness", "entities")
                .name("Mob Awareness")
                .description("Represents whether this mob is aware of its surroundings.",
                        "Turning this off is essentially like turning off the mobs AI.",
                        "Unaware mobs will still move if pushed, attacked, etc. but will not move or perform any actions on their own.",
                        "Unaware mobs will still be affected by gravity.",
                        "Unaware mobs may also have other unspecified behaviours disabled, such as drowning.")
                .examples("if mob awareness of target entity = true:",
                        "set mob awareness of target entity to false")
                .since("2.8.3")
                .register();
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof Mob mob) return mob.isAware();
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
        if (delta != null && delta[0] instanceof Boolean aware) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Mob mob) mob.setAware(aware);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "mob awareness";
    }

}
