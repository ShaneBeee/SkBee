package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Interpolation Start/Duration")
@Description({"Represents the interpolation start and duration times for a Display Entity.",
        "NOTE: Due to how these work, you will need to use integers as ticks instead of time spans.", Types.McWIKI})
@Examples({"set interpolation start of {_display} to -1",
        "set interpolation delay of {_display} to 200"})
@Since("2.8.0")
public class ExprDisplayInterpolation extends SimplePropertyExpression<Entity, Integer> {

    static {
        register(ExprDisplayInterpolation.class, Integer.class,
                "interpolation ((start|delay)|d:duration)", "entities");
    }

    private boolean duration;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.duration = parseResult.hasTag("d");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Integer convert(Entity entity) {
        if (!(entity instanceof Display display)) return null;
        return this.duration ? display.getInterpolationDuration() : display.getInterpolationDelay();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Integer.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Integer changeValue) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Display display) {
                    if (this.duration) {
                        display.setInterpolationDuration(changeValue);
                    } else {
                        display.setInterpolationDelay(changeValue);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "interpolation " + (this.duration ? "duration" : "start");
    }

}
