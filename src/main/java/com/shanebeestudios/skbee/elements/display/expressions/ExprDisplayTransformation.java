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
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Transformation")
@Description({"Represents the transformation of a Display Entity.", Types.McWIKI})
@Examples("set display transformation of {_display} to {_transformation}")
@Since("INSERT VERSION")
public class ExprDisplayTransformation extends SimplePropertyExpression<Display, Transformation> {

    static {
        register(ExprDisplayTransformation.class, Transformation.class,
                "display transformation", "displayentities");
    }

    @Override
    public @Nullable Transformation convert(Display display) {
        return display.getTransformation();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Transformation.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Transformation transformation) {
            for (Display display : getExpr().getArray(event)) {
                display.setTransformation(transformation);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Transformation> getReturnType() {
        return Transformation.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display transformation";
    }

}
