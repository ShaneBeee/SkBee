package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.FishHook.HookState;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Fish Hook - Hooked State")
@Description("Represents the hooked state of a fish hook.")
@Examples("if hook state of fish hook = bobbing:")
@Since("INSERT VERSION")
public class ExprFishHookState extends SimplePropertyExpression<Entity, HookState> {

    static {
        register(ExprFishHookState.class, HookState.class, "[fish] hook[ed] state", "entities");
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
