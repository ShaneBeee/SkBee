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

@Name("DisplayEntity - Shadow Radius/Strength")
@Description({"Represents the shadow radius/strength of a Display Entity.", Types.McWIKI})
@Examples({"set display shadow strength of {_display} to 3",
        "set display shadow radius of {_display} to 10"})
@Since("2.8.0")
public class ExprDisplayShadow extends SimplePropertyExpression<Entity, Number> {

    static {
        register(ExprDisplayShadow.class, Number.class,
                "[display] shadow (radius|s:strength)", "entities");
    }

    private boolean strength;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.strength = parseResult.hasTag("s");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Entity entity) {
        if (!(entity instanceof Display display)) return null;
        return this.strength ? display.getShadowStrength() : display.getShadowRadius();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Number n) {
            float changeValue = n.floatValue();
            for (Entity entity : getExpr().getArray(event)) {
                if (!(entity instanceof Display display)) continue;
                if (this.strength) {
                    display.setShadowStrength(changeValue);
                } else {
                    display.setShadowRadius(changeValue);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "shadow " + (this.strength ? "strength" : "radius");
    }

}
