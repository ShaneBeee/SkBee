package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Name("RayTrace - From Location")
@Description({"RayTrace from a location along a vector.",
    "Default max distance = 'maximum target block distance' in Skript's config.",
    "RaySize = entity bounding boxes will be uniformly expanded (or shrunk)",
    "by this value before doing collision checks (default = 0.0).",
    "IngorePassableBlocks = Will ignore passable but collidable blocks (ex. tall grass, signs, fluids, ..). [Added in SkBee 3.0.0]",
    "Ignoring Entities/EntityTypes = Will ignore the entities/entitytypes from the final ray. [Added in SkBee 3.5.0]",
    "Going through Blocks/ItemTypes = Will ignore the blocks/itemtypes from the final ray. [Added in SkBee INSERTVERSION]"})
@Examples("set {_ray} to ray trace from location of target block along vector(0.25,0.3,0) with max distance 50")
@Since("2.6.0")
public class ExprRayTraceFromLocation extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprRayTraceFromLocation.class, RayTraceResult.class, ExpressionType.COMBINED,
            "ray[ ]trace from %location% along %vectors% [with max distance %-number%] [with ray size %-number%] " +
                "[ignore:while ignoring passable blocks] [while ignoring %-entities/entitydatas%] " +
                "[while going through %-blocks/itemtypes%]");
    }

    private Expression<Location> location;
    private Expression<Vector> vectors;
    private Expression<Number> maxDistance;
    private Expression<Number> raySize;
    private boolean ignore;
    private Expression<?> ignoredEntities;
    private Expression<?> ignoredBlocks;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.location = (Expression<Location>) exprs[0];
        this.vectors = (Expression<Vector>) exprs[1];
        this.maxDistance = (Expression<Number>) exprs[2];
        this.raySize = (Expression<Number>) exprs[3];
        this.ignore = parseResult.hasTag("ignore");
        this.ignoredEntities = exprs[4];
        this.ignoredBlocks = exprs[5];
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected RayTraceResult @Nullable [] get(Event event) {
        Object[] ignoredEntities = this.ignoredEntities != null ? this.ignoredEntities.getArray(event) : null;
        Object[] ignoredBlocks = this.ignoredBlocks != null ? this.ignoredBlocks.getArray(event) : null;
        int maxDistance = SkriptConfig.maxTargetBlockDistance.value();
        if (this.maxDistance != null) {
            Number single = this.maxDistance.getSingle(event);
            if (single != null) maxDistance = single.intValue();
        }

        double raySize = 0.0;
        if (this.raySize != null) {
            Number raySizeNum = this.raySize.getSingle(event);
            if (raySizeNum != null) raySize = raySizeNum.doubleValue();
        }

        Location location = this.location.getSingle(event);
        if (location == null) return null;

        World world = location.getWorld();
        if (world == null) world = Bukkit.getWorlds().get(0);

        List<RayTraceResult> results = new ArrayList<>();
        for (Vector vector : this.vectors.getArray(event)) {
            RayTraceResult rayTraceResult = world.rayTrace(location, vector, maxDistance,
                FluidCollisionMode.NEVER, this.ignore, raySize,
                EntityUtils.filter(null, ignoredEntities), filteredBlocks(ignoredBlocks));
            results.add(rayTraceResult);
        }

        return results.toArray(new RayTraceResult[0]);
    }

    @Override
    public boolean isSingle() {
        return this.vectors.isSingle();
    }

    @Override
    public @NotNull Class<? extends RayTraceResult> getReturnType() {
        return RayTraceResult.class;
    }

    public static Predicate<Block> filteredBlocks(Object[] ignoredBlocks) {
        return block -> {
            for (Object ignoredBlock : ignoredBlocks) {
                if (ignoredBlock instanceof Block block1 && block == block1) return false;
                if (ignoredBlock instanceof ItemType itemType && block.getType() == itemType.getMaterial())
                    return false;
            }
            return true;
        };
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String max = this.maxDistance != null ? " with max distance " + this.maxDistance.toString(e, d) : "";
        String size = this.raySize != null ? " with ray size " + this.raySize.toString(e, d) : "";
        String ignore = this.ignore ? " while ignoring passable blocks" : "";
        String ignoredEntities = this.ignoredEntities != null ? (" while ignoring " + this.ignoredEntities.toString(e, d)) : "";
        String ignoredBlocks = this.ignoredBlocks != null ? (" while going through " + this.ignoredBlocks.toString(e, d)) : "";
        return "ray trace from " + this.location.toString(e, d) +
            " along " + this.vectors.toString(e, d) + max + size + ignore + ignoredEntities + ignoredBlocks;
    }

}
