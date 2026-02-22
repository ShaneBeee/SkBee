package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishHookHookedEntity extends SimplePropertyExpression<FishHook, Entity> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFishHookHookedEntity.class, Entity.class,
                "hooked entity", "entities")
            .name("Fish Hook - Hooked Entity")
            .description("Represents the entity hooked to the fish hook.")
            .examples("on fish:",
                "\tif fish state = caught entity:",
                "\t\tdelete hooked entity of fish event hook")
            .since("2.8.0")
            .register();
    }

    @Override
    public @Nullable Entity convert(FishHook fishHook) {
        return fishHook.getHookedEntity();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Entity.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Entity hooked = delta != null ? ((Entity) delta[0]) : null;

        for (FishHook fishHook : getExpr().getArray(event)) {
            fishHook.setHookedEntity(hooked);
        }
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "hooked entity";
    }

}
