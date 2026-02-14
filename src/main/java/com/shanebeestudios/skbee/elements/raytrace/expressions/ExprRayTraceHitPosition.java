package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRayTraceHitPosition extends SimplePropertyExpression<RayTraceResult, Vector> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprRayTraceHitPosition.class, Vector.class,
                "[ray[ ]trace] hit (position|vector)", "raytraceresults")
            .name("RayTrace - Hit Position")
            .description("Get the hit position resulting from a RayTrace (returns a vector).")
            .examples("set {_vec} to hit position of {_ray}")
            .since("2.6.0")
            .register();
    }

    @Override
    public @Nullable Vector convert(RayTraceResult rayTraceResult) {
        return rayTraceResult.getHitPosition();
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ray trace hit position";
    }

}
