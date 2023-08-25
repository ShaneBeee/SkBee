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
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Name("DisplayEntity - Translation")
@Description({"Represents the transformation translation of a Display Entity.", Types.McWIKI})
@Examples("set display translation of {_display} to vector(0,2,0)")
@Since("2.8.0")
public class ExprDisplayTranslation extends SimplePropertyExpression<Entity, Vector> {

    static {
        register(ExprDisplayTranslation.class, Vector.class, "display translation", "entities");
    }

    @Override
    public @Nullable Vector convert(Entity entity) {
        if (!(entity instanceof Display display)) return null;
        return Types.converToVector(display.getTransformation().getTranslation());
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
            Vector3f translation = Types.converToVector3f(vector);
            for (Entity entity : getExpr().getArray(event)) {
                if (!(entity instanceof Display display)) continue;

                Transformation oldTransform = display.getTransformation();
                Vector3f scale = oldTransform.getScale();
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
        return "display translation";
    }

}
