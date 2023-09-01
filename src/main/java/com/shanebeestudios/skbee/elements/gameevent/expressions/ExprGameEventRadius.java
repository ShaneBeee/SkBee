package com.shanebeestudios.skbee.elements.gameevent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Game Event - Radius")
@Description("Get/set the radius a game event will broadcast. Requires MC 1.17+")
@Examples({"on game event:",
        "\tset game event radius to 20"})
@Since("1.14.0")
public class ExprGameEventRadius extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprGameEventRadius.class, Number.class, ExpressionType.SIMPLE,
                "game[ ]event radius");
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(GenericGameEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in a generic game event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected Number[] get(Event event) {
        if (event instanceof GenericGameEvent gameEvent) {
            return new Number[]{gameEvent.getRadius()};
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(Number[].class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (event instanceof GenericGameEvent gameEvent && delta != null) {
            int radiusChange = delta[0] instanceof Number ? ((Number) delta[0]).intValue() : 0;
            int radius = gameEvent.getRadius();
            switch (mode) {
                case SET -> radius = radiusChange;
                case ADD -> radius += radiusChange;
                case REMOVE -> radius -= radiusChange;
                case DELETE -> radius = 0;
            }
            if (radius < 0) {
                radius = 0;
            }
            gameEvent.setRadius(radius);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "game event radius";
    }

}
