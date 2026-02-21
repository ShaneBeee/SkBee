package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.SkriptConfig;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprRayTraceFromEntity extends SimpleExpression<RayTraceResult> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprRayTraceFromEntity.class, RayTraceResult.class,
                "ray[ ]trace from %livingentities% [with max distance %-number%] [with ray size %-number%] " +
                    "[ignore:while ignoring passable blocks] [while (ignoring|allowing:only allowing) %-entities/entitydatas%]" +
                    "[while going through %-blocks/itemtypes%]")
            .name("RayTrace - From Entity")
            .description("RayTrace from an entity.",
                "Default max distance = 'maximum target block distance' in Skript's config.",
                "RaySize = entity bounding boxes will be uniformly expanded (or shrunk)",
                "by this value before doing collision checks (default = 0.0).",
                "IngorePassableBlocks = Will ignore passable but collidable blocks (ex. tall grass, signs, fluids, ..).",
                "Ignoring/Only Allowing Entities/EntityTypes = Will ignore/only allow the entities/entitytypes from the final ray.",
                "Going through Blocks/ItemTypes = Will ignore the blocks/itemtypes from the final ray.")
            .examples("set {_ray} to ray trace from player with max distance 25",
                "set {_ray} to ray trace from player with max distance 25 while ignoring passable blocks",
                "set {_rays::*} to raytrace from all players with ray size 0.1")
            .since("2.6.0")
            .register();
    }

    private Expression<LivingEntity> entities;
    private Expression<Number> maxDistance;
    private Expression<Number> raySize;
    private boolean ignore;
    private Expression<?> filteredEntities;
    private boolean allowing;
    private Expression<?> ignoredBlocks;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<LivingEntity>) exprs[0];
        this.maxDistance = (Expression<Number>) exprs[1];
        this.raySize = (Expression<Number>) exprs[2];
        this.ignore = parseResult.hasTag("ignore");
        this.allowing = parseResult.hasTag("allowing");
        this.filteredEntities = exprs[3];
        this.ignoredBlocks = exprs[4];
        return true;
    }

    @SuppressWarnings({"UnstableApiUsage"})
    @Override
    protected @Nullable RayTraceResult[] get(Event event) {
        Object[] ignoredEntities = this.filteredEntities != null ? this.filteredEntities.getArray(event) : null;
        Object[] ignoredBlocks = this.ignoredBlocks != null ? this.ignoredBlocks.getArray(event) : null;
        double maxDistance = SkriptConfig.maxTargetBlockDistance.value();
        if (this.maxDistance != null) {
            Number maxDistanceNum = this.maxDistance.getSingle(event);
            if (maxDistanceNum != null) maxDistance = maxDistanceNum.doubleValue();
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
                EntityUtils.filter(livingEntity, ignoredEntities, this.allowing),
                ExprRayTraceFromLocation.filteredBlocks(ignoredBlocks));
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
        String size = this.raySize != null ? " with ray size " + this.raySize.toString(e, d) : "";
        String ignore = this.ignore ? " while ignoring passable blocks" : "";
        String filter = this.allowing ? "only allowing " : "ignoring ";
        String ignoredEntities = this.filteredEntities != null ? (" while " + filter + this.filteredEntities.toString(e, d)) : "";
        String ignoredBlocks = this.ignoredBlocks != null ? (" while going through " + this.ignoredBlocks.toString(e, d)) : "";
        return "ray trace from " + this.entities.toString(e, d) + max + size + ignore + ignoredEntities + ignoredBlocks;
    }

}
