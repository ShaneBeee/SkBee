package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - View Range")
@Description({"Represents the view range of a Display Entity.", Types.McWIKI})
@Examples("set display view range of {_display} to 500")
@Since("2.8.0")
public class ExprDisplayViewRange extends SimplePropertyExpression<Entity, Number> {

    static {
        register(ExprDisplayViewRange.class, Number.class, "display view range", "entities");
    }

    @Override
    public @Nullable Number convert(Entity entity) {
        if (entity instanceof Display display) return display.getViewRange();
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
            float changeValue = num.floatValue();
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Display display) display.setViewRange(changeValue);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "view range";
    }

}
