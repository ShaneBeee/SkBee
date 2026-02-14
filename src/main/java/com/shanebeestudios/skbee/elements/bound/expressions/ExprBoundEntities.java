package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprBoundEntities extends SimpleExpression<Entity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBoundEntities.class, Entity.class,
                "[(all [[of] the]|the)] bound %*entitydatas% (of|in|within) %bounds%",
                "[(all [[of] the]|the)] %*entitydatas% (of|in|within) bound[s] %bounds%")
            .name("Bound - Entities")
            .description("Get all of the entities within a bound.",
                "NOTE: If the chunk in a bound is unloaded, entities will also be unloaded.")
            .examples("set {_b} to bound with id \"my-bound\"",
                "loop bound entities in {_b}:",
                "\tif loop-entity is a cow or pig:",
                "\t\tkill loop-entity")
            .since("1.15.0")
            .register();
    }

    private Expression<EntityData<?>> entityDatas;
    private Expression<Bound> bounds;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entityDatas = (Expression<EntityData<?>>) exprs[0];
        this.bounds = (Expression<Bound>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected Entity[] get(Event event) {
        List<Entity> entities = new ArrayList<>();
        for (Bound bound : this.bounds.getArray(event)) {
            for (EntityData<?> entityData : this.entityDatas.getArray(event)) {
                Class<? extends Entity> type = entityData.getType();
                entities.addAll(bound.getEntities(type));
            }
        }
        return entities.toArray(new Entity[0]);
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
    public @NotNull String toString(Event e, boolean d) {
        return "bound " + this.entityDatas.toString(e, d) + " of " + this.bounds.toString(e, d);
    }

}
