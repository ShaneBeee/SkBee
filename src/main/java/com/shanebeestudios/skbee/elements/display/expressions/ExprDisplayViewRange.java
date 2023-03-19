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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("DisplayEntity - View Range")
@Description({"Represents the view range of a Display Entity.", Types.McWIKI})
@Examples("set view range of {_display} to 500")
@Since("INSERT VERSION")
public class ExprDisplayViewRange extends SimplePropertyExpression<Display, Float> {

    static {
        register(ExprDisplayViewRange.class, Float.class, "view range", "displayentities");
    }

    @Override
    public @Nullable Float convert(Display display) {
        return display.getViewRange();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Float.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Float changeValue) {
            for (Display display : getExpr().getArray(event)) {
                display.setViewRange(changeValue);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Float> getReturnType() {
        return Float.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "view range";
    }

}
