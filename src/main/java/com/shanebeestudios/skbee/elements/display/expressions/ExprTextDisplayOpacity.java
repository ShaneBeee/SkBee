package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Opacity")
@Description({"Represents the text opacity of a Text Display Entity.", Types.McWIKI})
@Examples("set display text opacity of {_display} to 50")
@Since("2.8.0")
public class ExprTextDisplayOpacity extends SimplePropertyExpression<Entity, Number> {

    static {
        register(ExprTextDisplayOpacity.class, Number.class, "[display] text opacity", "entities");
    }

    @Override
    public @Nullable Number convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) return (int) textDisplay.getTextOpacity();
        return null;
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
        if (delta != null && delta[0] instanceof Number num) {
            byte opacity = num.byteValue();
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof TextDisplay textDisplay) {
                    textDisplay.setTextOpacity(opacity);
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
        return "text opacity";
    }

}
