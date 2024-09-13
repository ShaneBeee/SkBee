package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - Entities")
@Description({"Get all the entities within a bound.",
    "NOTE: If the chunk in a bound is unloaded, entities will also be unloaded."})
@Examples({"set {_b} to bound with id \"my-bound\"",
    "loop entities in bound {_b}:",
    "\tif loop-entity is a cow or pig:",
    "\t\tkill loop-entity"})
@Since("1.15.0")
public class ExprBoundEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprBoundEntities.class, Entity.class, ExpressionType.SIMPLE,
            "[(all [[of] the]|the)] %*entitydatas% (of|in|within) bound[s] %bounds%");
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
        return this.entityDatas.toString(e, d) + " of bound[s] " + this.bounds.toString(e, d);
    }

}
