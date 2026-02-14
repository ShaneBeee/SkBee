package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRayTraceHitBlockFace extends SimplePropertyExpression<RayTraceResult, BlockFace> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprRayTraceHitBlockFace.class, BlockFace.class,
                "[ray[ ]trace] hit blockface", "raytraceresults")
            .name("RayTrace - Hit BlockFace")
            .description("Gets the hit BlockFace resulting from a RayTrace.")
            .examples("set {_face} to ray trace hit blockface of {_ray}")
            .since("2.6.0")
            .register();
    }

    @Override
    public @Nullable BlockFace convert(RayTraceResult rayTraceResult) {
        return rayTraceResult.getHitBlockFace();
    }

    @Override
    public @NotNull Class<? extends BlockFace> getReturnType() {
        return BlockFace.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ray trace hit blockface";
    }

}
