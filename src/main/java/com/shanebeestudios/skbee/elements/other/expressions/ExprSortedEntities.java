package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Name("Entities Sorted by Distance")
@Description("Sort entities by distance from a central location.")
@Examples({"loop all entities sorted by distance from player:",
    "set {_sort::*} to all mobs sorted by distance from {_loc}",
    "set {_p::*} to all players sorted by distance from {spawn}",
    "loop all mobs sorted by distance from player:"})
@Since("3.0.0")
public class ExprSortedEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprSortedEntities.class, Entity.class, ExpressionType.COMBINED,
            "%entities% sorted by distance from %location%");
    }

    private Expression<Entity> entities;
    private Expression<Location> location;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.location = (Expression<Location>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Entity[] get(Event event) {
        Location location = this.location.getSingle(event);
        if (location == null) {
            error("Location is not set: " + this.location.toString(event, true));
            return null;
        }
        World world = location.getWorld();
        if (world == null) {
            error("Unknown world for location: " + Classes.toString(location));
            return null;
        }
        List<Entity> collect = Arrays.stream(this.entities.getArray(event))
            .filter(entity -> entity.getWorld() == world) // Entities have to be in the same world
            .sorted(Comparator.comparing(entity -> entity.getLocation().distanceSquared(location)))
            .toList();
        return collect.toArray(new Entity[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.entities.toString(e, d) + " sorted by distance from " + this.location.toString(e, d);
    }

}
