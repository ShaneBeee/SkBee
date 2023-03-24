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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Name("Quaternion - Values")
@Description("Represents the x/y/z/w values of a quaternion.")
@Examples({"add 1 to quat-x of {_q}",
        "remove 1 from quat-x of {_q}",
        "reset quat-x of {_q}",
        "set {_x} to quat-x of display right rotation of {_display}"})
@Since("INSERT VERSION")
public class ExprQuaternionElements extends SimplePropertyExpression<Quaternionf, Float> {

    static {
        register(ExprQuaternionElements.class, Float.class,
                "quat[ernion]-(x|1:y|2:z|3:w)", "quaternions");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Float convert(Quaternionf quaternionf) {
        return switch (this.pattern) {
            case 1 -> quaternionf.y;
            case 2 -> quaternionf.z;
            case 3 -> quaternionf.w;
            default -> quaternionf.x;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET, ADD, REMOVE, RESET -> {
                return CollectionUtils.array(Float.class);
            }
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        float changeValue = delta != null && delta[0] instanceof Float ? (float) delta[0] : this.pattern == 3 ? 1.0f : 0.0f;

        for (Quaternionf quaternionf : getExpr().getArray(event)) {
            float oldValue = switch (this.pattern) {
                case 1 -> quaternionf.y;
                case 2 -> quaternionf.z;
                case 3 -> quaternionf.w;
                default -> quaternionf.x;
            };
            switch (mode) {
                case ADD -> changeValue += oldValue;
                case REMOVE -> changeValue = oldValue - changeValue;
            }
            switch (this.pattern) {
                case 1 -> quaternionf.y = changeValue;
                case 2 -> quaternionf.z = changeValue;
                case 3 -> quaternionf.w = changeValue;
                default -> quaternionf.x = changeValue;
            }
        }
    }

    @Override
    public @NotNull Class<? extends Float> getReturnType() {
        return Float.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String value = switch (this.pattern) {
            case 1 -> "y";
            case 2 -> "z";
            case 3 -> "w";
            default -> "x";
        };
        return "quat-" + value;
    }

}
