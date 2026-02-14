package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRayTraceHitLocation extends SimplePropertyExpression<RayTraceResult, Location> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprRayTraceHitLocation.class, Location.class,
                "[ray[ ]trace] hit location", "raytraceresults")
            .name("RayTrace - Hit Location")
            .description("Get the hit location resulting from a RayTrace.")
            .examples("set {_hit} to hit location of {_ray}")
            .since("2.6.0")
            .register();
    }

    @Override
    public @Nullable Location convert(RayTraceResult rayTraceResult) {
        Block hitBlock = rayTraceResult.getHitBlock();
        Entity hitEntity = rayTraceResult.getHitEntity();

        World world;
        if (hitBlock != null) world = hitBlock.getWorld();
        else if (hitEntity != null) world = hitEntity.getWorld();
        else return null;

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
