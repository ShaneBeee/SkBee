package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Name("Pathfinding - Path Points")
@Description("Get all the points along an entity's pathfinding path. Requires Paper 1.13+")
@Examples("set {_path::*} to path points of last spawned sheep")
@Since("1.5.0")
public class ExprPath extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprPath.class, Location.class, ExpressionType.COMBINED,
                "path [points] of %livingentities%");
    }

    private Expression<LivingEntity> entities;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!Skript.classExists("com.destroystokyo.paper.entity.Pathfinder")) {
            Skript.error("This expression requires a PaperMC server or a fork of.");
            return false;
        }
        entities = (Expression<LivingEntity>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Location[] get(@NotNull Event e) {
        List<Location> locations = new ArrayList<>();
        for (LivingEntity entity : entities.getArray(e)) {
            if (entity instanceof Mob) {
                PathResult result = ((Mob) entity).getPathfinder().getCurrentPath();
                if (result != null)
                    locations.addAll(result.getPoints());
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
