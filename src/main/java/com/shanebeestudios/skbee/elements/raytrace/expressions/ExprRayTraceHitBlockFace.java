package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("RayTrace - Hit BlockFace")
@Description("Gets the hit BlockFace resulting from a RayTrace.")
@Examples("set {_face} to ray trace hit blockface of {_ray}")
@Since("2.6.0")
public class ExprRayTraceHitBlockFace extends SimplePropertyExpression<RayTraceResult, BlockFace> {

    static {
        register(ExprRayTraceHitBlockFace.class, BlockFace.class,
                "[ray[ ]trace] hit blockface", "raytraceresults");
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
