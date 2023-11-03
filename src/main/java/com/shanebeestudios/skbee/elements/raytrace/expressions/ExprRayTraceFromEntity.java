package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("RayTrace - From Entity")
@Description({"RayTrace from an entity.",
        "\nDefault max distance = 'maximum target block distance' in Skript's config.",
        "\nRaySize = entity bounding boxes will be uniformly expanded (or shrunk)",
        "by this value before doing collision checks (default = 0.0).",
        "\nIngorePassableBlocks = Will ignore passable but collidable blocks (ex. tall grass, signs, fluids, ..)"})
@Examples({"set {_ray} to ray trace from player with max distance 25",
        "set {_ray} to ray trace from player with max distance 25 while ignoring passable blocks",
        "set {_rays::*} to raytrace from all players with ray size 0.1"})
@Since("2.6.0")
public class ExprRayTraceFromEntity extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprRayTraceFromEntity.class, RayTraceResult.class, ExpressionType.COMBINED,
                "ray[ ]trace from %livingentities% [with max distance %-number%] [with ray size %-number%] [ignore:while ignoring passable blocks]");
    }

    private Expression<LivingEntity> entities;
    private Expression<Number> maxDistance;
    private Expression<Number> raySize;
    private boolean ignore;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<LivingEntity>) exprs[0];
        this.maxDistance = (Expression<Number>) exprs[1];
        this.raySize = (Expression<Number>) exprs[2];
        this.ignore = parseResult.hasTag("ignore");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable RayTraceResult[] get(Event event) {
        int maxDistance = SkriptConfig.maxTargetBlockDistance.value();
        if (this.maxDistance != null) {
            Number maxDistanceNum = this.maxDistance.getSingle(event);
            if (maxDistanceNum != null) maxDistance = maxDistanceNum.intValue();
        }

        double raySize = 0.0;
        if (this.raySize != null) {
            Number raySizeNum = this.raySize.getSingle(event);
            if (raySizeNum != null) raySize = raySizeNum.doubleValue();
        }

        List<RayTraceResult> results = new ArrayList<>();

        for (LivingEntity livingEntity : this.entities.getArray(event)) {

            World world = livingEntity.getWorld();
            Location location = livingEntity.getEyeLocation();
            Vector direction = location.getDirection();

            RayTraceResult rayTraceResult = world.rayTrace(location, direction, maxDistance,
                    FluidCollisionMode.NEVER, this.ignore, raySize,
                    entity -> entity != livingEntity);

            results.add(rayTraceResult);
        }

        return results.toArray(new RayTraceResult[0]);
    }

    @Override
    public boolean isSingle() {
        return this.entities.isSingle();
    }

    @Override
    public @NotNull Class<? extends RayTraceResult> getReturnType() {
        return RayTraceResult.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String max = this.maxDistance != null ? " with max distance " + this.maxDistance.toString(e, d) : "";
        String size = this.raySize != null ? " with ray size " + this.raySize.toString(e,d) : "";
        String ignore = this.ignore ? " while ignoring passable blocks" : "";
        return "ray trace from " + this.entities.toString(e, d) + max + size + ignore;
    }

}
