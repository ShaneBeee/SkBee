package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("RayTrace - Hit Entity")
@Description("Gets the hit entity resulting from a RayTrace.")
@Examples("set {_hit} to raytrace hit entity of {_ray}")
@Since("INSERT VERSION")
public class ExprRayTraceHitEntity extends SimplePropertyExpression<RayTraceResult, Entity> {

    static {
        register(ExprRayTraceHitEntity.class, Entity.class,
                "[ray[ ]trace] hit entity", "raytraceresults");
    }

    @Override
    public @Nullable Entity convert(RayTraceResult rayTraceResult) {
        return rayTraceResult.getHitEntity();
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ray trace hit entity";
    }

}
