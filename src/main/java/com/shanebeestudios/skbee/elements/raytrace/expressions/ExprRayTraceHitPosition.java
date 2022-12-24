package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("RayTrace - Hit Position")
@Description("Get the hit position resulting from a RayTrace (returns a vector).")
@Examples("set {_vec} to hit position of {_ray}")
@Since("2.6.0")
public class ExprRayTraceHitPosition extends SimplePropertyExpression<RayTraceResult, Vector> {

    static {
        register(ExprRayTraceHitPosition.class, Vector.class,
                "[ray[ ]trace] hit (position|vector)", "raytraceresults");
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
