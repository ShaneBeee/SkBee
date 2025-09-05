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

@Name("RayTrace - From Entity")
@Description({"RayTrace from an entity.",
    "Default max distance = 'maximum target block distance' in Skript's config.",
    "RaySize = entity bounding boxes will be uniformly expanded (or shrunk)",
    "by this value before doing collision checks (default = 0.0).",
    "IngorePassableBlocks = Will ignore passable but collidable blocks (ex. tall grass, signs, fluids, ..). [Added in SkBee 3.0.0]",
    "Ignoring Entities/EntityTypes = Will ignore the entities/entitytypes from the final ray. [Added in SkBee 3.5.0]",
    "Going through Blocks/ItemTypes = Will ignore the blocks/itemtypes from the final ray. [Added in SkBee INSERTVERSION]"})
@Examples({"set {_ray} to ray trace from player with max distance 25",
    "set {_ray} to ray trace from player with max distance 25 while ignoring passable blocks",
    "set {_rays::*} to raytrace from all players with ray size 0.1"})
@Since("2.6.0")
public class ExprRayTraceFromEntity extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprRayTraceFromEntity.class, RayTraceResult.class, ExpressionType.COMBINED,
            "ray[ ]trace from %livingentities% [with max distance %-number%] [with ray size %-number%] " +
                "[ignore:while ignoring passable blocks] [while ignoring %-entities/entitydatas%]" +
                "[while going through %-blocks/itemtypes%]");
    }

    private Expression<LivingEntity> entities;
    private Expression<Number> maxDistance;
    private Expression<Number> raySize;
    private boolean ignore;
    private Expression<?> ignoredEntities;
    private Expression<?> ignoredBlocks;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<LivingEntity>) exprs[0];
        this.maxDistance = (Expression<Number>) exprs[1];
        this.raySize = (Expression<Number>) exprs[2];
        this.ignore = parseResult.hasTag("ignore");
        this.ignoredEntities = exprs[3];
        this.ignoredBlocks = exprs[4];
        return true;
    }

    @SuppressWarnings({"UnstableApiUsage"})
    @Override
    protected @Nullable RayTraceResult[] get(Event event) {
        Object[] ignoredEntities = this.ignoredEntities != null ? this.ignoredEntities.getArray(event) : null;
        Object[] ignoredBlocks = this.ignoredBlocks != null ? this.ignoredBlocks.getArray(event) : null;
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
                EntityUtils.filter(livingEntity, ignoredEntities), ExprRayTraceFromLocation.filteredBlocks(ignoredBlocks));
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
        String ignoredEntities = this.ignoredEntities != null ? (" while ignoring " + this.ignoredEntities.toString(e, d)) : "";
        String ignoredBlocks = this.ignoredBlocks != null ? (" while going through " + this.ignoredBlocks.toString(e, d)) : "";
        return "ray trace from " + this.entities.toString(e, d) + max + size + ignore + ignoredEntities + ignoredBlocks;
    }

}
