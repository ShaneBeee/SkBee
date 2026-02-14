package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRayTraceHitBlock extends SimplePropertyExpression<RayTraceResult, Block> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprRayTraceHitBlock.class, Block.class,
                "[ray[ ]trace] hit block", "raytraceresults")
            .name("RayTrace - Hit Block")
            .description("Gets the hit block resulting from a RayTrace.")
            .examples("set {_hit} to ray trace hit block of {_ray}")
            .since("2.6.0")
            .register();
    }

    @Override
    public @Nullable Block convert(RayTraceResult rayTraceResult) {
        return rayTraceResult.getHitBlock();
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ray trace hit block";
    }

}
