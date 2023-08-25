package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("RayTrace - Hit Block")
@Description("Gets the hit block resulting from a RayTrace.")
@Examples("set {_hit} to ray trace hit block of {_ray}")
@Since("2.6.0")
public class ExprRayTraceHitBlock extends SimplePropertyExpression<RayTraceResult, Block> {

    static {
        register(ExprRayTraceHitBlock.class, Block.class,
                "[ray[ ]trace] hit block", "raytraceresults");
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
