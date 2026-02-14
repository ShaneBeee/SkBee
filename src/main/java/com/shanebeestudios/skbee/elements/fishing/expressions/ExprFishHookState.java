package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.FishHook.HookState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishHookState extends SimplePropertyExpression<Entity, HookState> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFishHookState.class, HookState.class,
                "[fish] hook[ed] state", "entities")
            .name("Fish Hook - Hooked State")
            .description("Represents the hooked state of a fish hook.")
            .examples("if hook state of fish hook = bobbing:")
            .since("2.8.0")
            .register();
    }

    @Override
    public @Nullable HookState convert(Entity entity) {
        if (entity instanceof FishHook fishHook) {
            return fishHook.getState();
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends HookState> getReturnType() {
        return HookState.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "fish hooked state";
    }

}
