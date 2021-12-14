package com.shanebeestudios.skbee.elements.gameevent.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
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
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.event.world.GenericGameEvent;
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


    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(GenericGameEvent.class)) {
            Skript.error("The expression 'game event radius' can only be used in a generic game event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    protected Number[] get(Event event) {
        if (event instanceof GenericGameEvent) {
            GenericGameEvent gameEvent = (GenericGameEvent) event;
            return new Number[]{gameEvent.getRadius()};
        }
        return null;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET:
            case ADD:
            case REMOVE:
            case DELETE:
                return CollectionUtils.array(Number[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (event instanceof GenericGameEvent && delta != null) {
            GenericGameEvent gameEvent = (GenericGameEvent) event;
            int radiusChange = delta[0] instanceof Number ? ((Number) delta[0]).intValue() : 0;
            int radius = gameEvent.getRadius();
            switch (mode) {
                case SET:
                    radius = radiusChange;
                    break;
                case ADD:
                    radius += radiusChange;
                    break;
                case REMOVE:
                    radius -= radiusChange;
                    break;
                case DELETE:
                    radius = 0;
                    break;
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
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "game event radius";
    }

}
