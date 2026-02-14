package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRayTraceHitEntity extends SimplePropertyExpression<RayTraceResult, Entity> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprRayTraceHitEntity.class, Entity.class,
                "[ray[ ]trace] hit entity", "raytraceresults")
            .name("RayTrace - Hit Entity")
            .description("Gets the hit entity resulting from a RayTrace.")
            .examples("set {_hit} to raytrace hit entity of {_ray}")
            .since("2.6.0")
            .register();
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
