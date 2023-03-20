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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Name("DisplayEntity - Scale")
@Description({"Represents the transformation scale of a Display Entity.", Types.McWIKI})
@Examples("set display scale of {_display} to vector(5,5,5)")
@Since("INSERT VERSION")
public class ExprDisplayScale extends SimplePropertyExpression<Display, Vector> {

    static {
        register(ExprDisplayScale.class, Vector.class, "display scale", "displayentities");
    }

    @Override
    public @Nullable Vector convert(Display display) {
        return Types.converToVector(display.getTransformation().getScale());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Vector.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Vector vector) {
            Vector3f scale = Types.converToVector3f(vector);
            for (Display display : getExpr().getArray(event)) {
                Transformation oldTransform = display.getTransformation();
                Vector3f translation = oldTransform.getTranslation();
                Quaternionf leftRotation = oldTransform.getLeftRotation();
                Quaternionf rightRotation = oldTransform.getRightRotation();
                Transformation newTransform = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(newTransform);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display scale";
    }

}
