package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExprPath extends SimpleExpression<Location> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprPath.class, Location.class,
            "path [points] of %livingentities%")
            .name("Pathfinding - Path Points")
            .description("Get all the points along an entity's pathfinding path. Requires Paper 1.13+")
            .examples("set {_path::*} to path points of last spawned sheep")
            .since("1.5.0")
            .register();
    }

    private Expression<LivingEntity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        entities = (Expression<LivingEntity>) exprs[0];
        return true;
    }

    @Override
    protected Location[] get(@NotNull Event e) {
        List<Location> locations = new ArrayList<>();
        for (LivingEntity entity : entities.getArray(e)) {
            if (entity instanceof Mob mob) {
                PathResult result = mob.getPathfinder().getCurrentPath();
                if (result != null) locations.addAll(result.getPoints());
            }
        }
        return locations.toArray(new Location[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "path points of " + this.entities.toString(e, d);
    }

}
