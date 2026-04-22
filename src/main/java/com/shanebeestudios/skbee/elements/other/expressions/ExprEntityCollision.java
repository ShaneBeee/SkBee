package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprEntityCollision extends SimplePropertyExpression<LivingEntity, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityCollision.class, Boolean.class,
                "(collision|collidable) status", "livingentities")
            .name("Entity Collision")
            .description("Get/set whether this entity will be subject to collisions with other entities.",
                "Note that the client may predict the collision between itself and another entity, " +
                    "resulting in this flag not working for player collisions.",
                "This expression should therefore only be used to set the collision status of non-player entities.",
                "Resetting will set the collision status to true.")
            .examples("set collision status of {_entity} to false",
                "set collision status of all entities to false")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Boolean convert(LivingEntity entity) {
        return entity.isCollidable();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        if (delta == null || delta.length == 0) return;
        boolean bool = delta != null && delta[0] instanceof Boolean b ? b : true;

        for (LivingEntity entity : getExpr().getArray(event)) {
            if (entity == null) continue;
            entity.setCollidable(bool);
        }
    }

    @Override
    protected String getPropertyName() {
        return "collision status";
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

}
