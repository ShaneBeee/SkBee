package com.shanebeestudios.skbee.elements.raytrace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
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
import org.bukkit.event.Event;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("RayTrace - From Location")
@Description({"RayTrace from a location along a vector.",
        "\nDefault max distance = 'maximum target block distance' in Skript's config.",
        "\nRaySize = entity bounding boxes will be uniformly expanded (or shrunk)",
        "by this value before doing collision checks (default = 0.0).",
        "\nIngorePassableBlocks = Will ignore passable but collidable blocks (ex. tall grass, signs, fluids, ..). " +
                "[Added in SkBee 3.0.0]",
        "\nIgnoring Entities/EntityTypes = Will ignore the entities/entitytypes from the final ray. " +
                "[Added in SkBee INSERT VERSION]"})
@Examples("set {_ray} to ray trace from location of target block along vector(0.25,0.3,0) with max distance 50")
@Since("2.6.0")
public class ExprRayTraceFromLocation extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprRayTraceFromLocation.class, RayTraceResult.class, ExpressionType.COMBINED,
                "ray[ ]trace from %location% along %vectors% [with max distance %-number%] [with ray size %-number%]  " +
                        "[ignore:while ignoring passable blocks] [while ignoring %-entities/entitydatas%]");
    }

    private Expression<Location> location;
    private Expression<Vector> vectors;
    private Expression<Number> maxDistance;
    private Expression<Number> raySize;
    private boolean ignore;
    private Expression<?> ignored;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.location = (Expression<Location>) exprs[0];
        this.vectors = (Expression<Vector>) exprs[1];
        this.maxDistance = (Expression<Number>) exprs[2];
        this.raySize = (Expression<Number>) exprs[3];
        this.ignore = parseResult.hasTag("ignore");
        this.ignored = exprs[4];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected RayTraceResult @Nullable [] get(Event event) {
        Object[] ignored = this.ignored != null ? this.ignored.getArray(event) : null;
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
                    EntityUtils.filter(null, ignored));
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

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String max = this.maxDistance != null ? " with max distance " + this.maxDistance.toString(e, d) : "";
        String size = this.raySize != null ? " with ray size " + this.raySize.toString(e, d) : "";
        String ignore = this.ignore ? " while ignoring passable blocks" : "";
        String ignored = this.ignored != null ? (" while ignoring " + this.ignored.toString(e, d)) : "";
        return "ray trace from " + this.location.toString(e, d) +
                " along " + this.vectors.toString(e, d) + max + size + ignore + ignored;
    }

}
