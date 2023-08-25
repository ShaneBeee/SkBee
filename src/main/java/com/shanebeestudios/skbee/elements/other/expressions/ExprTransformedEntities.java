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
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Transformed Entities")
@Description("List of entities that transformed during an entity transform event.")
@Examples({"on entity transform:",
        "\tif event-entity is a villager:",
        "\t\tif transformed entity is a witch:",
        "\t\t\tspawn a turtle at event-location"})
@Since("2.5.3")
public class ExprTransformedEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprTransformedEntities.class, Entity.class, ExpressionType.SIMPLE,
                "transformed entit(y|ies)");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(EntityTransformEvent.class)) {
            Skript.error("Cannot use 'transformed entities' outside of the entity transform event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Entity[] get(Event event) {
        if (event instanceof EntityTransformEvent entityTransformEvent) {
            return entityTransformEvent.getTransformedEntities().toArray(new Entity[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "transformed entities";
    }

}
