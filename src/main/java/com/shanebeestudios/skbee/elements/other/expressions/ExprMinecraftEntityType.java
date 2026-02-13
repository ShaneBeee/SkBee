package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMinecraftEntityType extends SimplePropertyExpression<Entity, EntityType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprMinecraftEntityType.class, EntityType.class,
                        "minecraft entity type", "entities")
                .name("Minecraft - EntityType")
                .description("Get the Minecraft EntityType from an entity.")
                .examples("set {_type} to minecraft entity type of target entity")
                .since("3.5.0")
                .register();
    }

    @Override
    public @Nullable EntityType convert(Entity entity) {
        return entity.getType();
    }

    @Override
    public @NotNull Class<? extends EntityType> getReturnType() {
        return EntityType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "minecraft entity type";
    }

}
