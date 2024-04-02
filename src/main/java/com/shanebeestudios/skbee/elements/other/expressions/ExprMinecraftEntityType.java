package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Minecraft - EntityType")
@Description("Get the Minecraft EntityType from an entity.")
@Examples("set {_type} to minecraft entity type of target entity")
@Since("INSERT VERSION")
public class ExprMinecraftEntityType extends SimplePropertyExpression<Entity, EntityType> {

    static {
        register(ExprMinecraftEntityType.class, EntityType.class,
                "minecraft entity type", "entities");
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
