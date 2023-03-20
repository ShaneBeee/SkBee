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
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Name("DisplayEntity - Rotation")
@Description({"Represents the transformation left/right rotation of a Display Entity.", Types.McWIKI})
@Examples({"set display left rotation of {_display} to vector4(0,1,1,0)",
        "set display right rotation of {_display} to vector4(1,0,0,5)"})
@Since("INSERT VERSION")
public class ExprDisplayRotation extends SimplePropertyExpression<Entity, Quaternionf> {

    static {
        register(ExprDisplayRotation.class, Quaternionf.class,
                "display (left|r:right) rotation", "entities");
    }

    private boolean right;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.right = parseResult.hasTag("r");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Quaternionf convert(Entity entity) {
        if (!(entity instanceof Display display)) return null;
        Transformation transformation = display.getTransformation();
        return this.right ? transformation.getRightRotation() : transformation.getLeftRotation();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Quaternionf.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Quaternionf vec4f) {
            for (Entity entity : getExpr().getArray(event)) {
                if (!(entity instanceof Display display)) continue;

                Transformation oldTransform = display.getTransformation();
                Vector3f translation = oldTransform.getTranslation();
                Vector3f scale = oldTransform.getScale();
                Quaternionf leftRotation = this.right ? oldTransform.getLeftRotation() : vec4f;
                Quaternionf rightRotation = this.right ? vec4f : oldTransform.getRightRotation();
                Transformation newTransform = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(newTransform);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Quaternionf> getReturnType() {
        return Quaternionf.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display " + (this.right ? "right" : "left") + " rotation";
    }

}
