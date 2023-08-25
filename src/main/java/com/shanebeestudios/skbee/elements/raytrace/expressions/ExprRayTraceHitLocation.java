package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("RayTrace - Hit Location")
@Description("Get the hit location resulting from a RayTrace.")
@Examples("set {_hit} to hit location of {_ray}")
@Since("2.6.0")
public class ExprRayTraceHitLocation extends SimplePropertyExpression<RayTraceResult, Location> {

    static {
        register(ExprRayTraceHitLocation.class, Location.class,
                "[ray[ ]trace] hit location", "raytraceresults");
    }

    @Override
    public @Nullable Location convert(RayTraceResult rayTraceResult) {
        Block hitBlock = rayTraceResult.getHitBlock();
        if (hitBlock == null) return null;

        World world = hitBlock.getWorld();
        return rayTraceResult.getHitPosition().toLocation(world);
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "ray trace hit location";
    }

}
