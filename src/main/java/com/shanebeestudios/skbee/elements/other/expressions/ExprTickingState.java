package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTickingState extends SimplePropertyExpression<Entity, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprTickingState.class, Boolean.class, "tick[ing] state", "entities")
            .name("Entity Ticking State")
            .description("Represents whether or not an entity will tick.",
                "Currently this only works for ArmorStands.")
            .examples("spawn an armor stand at player:",
                "\tset ticking state of entity to false")
            .since("2.13.0")
            .register();
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof ArmorStand armorStand) {
            return armorStand.canTick();
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean canTick) {
            for (Entity entity : getExpr().getArray(e)) {
                if (entity instanceof ArmorStand armorStand) {
                    armorStand.setCanTick(canTick);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ticking state";
    }

}
