package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class CondEntityWouldCollideWith extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondEntityWouldCollideWith.class,
                "%entities% (would|will) collide (with|at) %entity/location/block%",
                "%entities% (wouldn't|would not|will not) collide (with|at) %entity/location/block%")
            .name("Entity Would Collide With")
            .description("Check if an entity would collide with another entity or block.",
                "Optionally you can check if an entity would collide with anything at a location.")
            .examples("if player would collide at {_loc}:",
                "if {_entity} would collide with {_block}:")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Entity> entities;
    private Expression<?> collidable;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) expressions[0];
        this.collidable = expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        Object collidable = this.collidable.getSingle(event);
        return this.entities.check(event, entity -> {
            if (collidable instanceof Entity e) {
                return entity.wouldCollideUsing(e.getBoundingBox());
            } else if (collidable instanceof Location loc) {
                return entity.collidesAt(loc);
            } else if (collidable instanceof Block block) {
                return entity.wouldCollideUsing(block.getBoundingBox());
            }
            return false;
        }, isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String n = isNegated() ? " will not" : " will";
        return this.entities.toString(event, debug) + n + " collide with " + this.collidable.toString(event, debug);
    }

}
