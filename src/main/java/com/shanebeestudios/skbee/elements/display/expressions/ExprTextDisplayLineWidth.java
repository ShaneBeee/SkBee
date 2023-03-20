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
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Line Width")
@Description({"Represents the line width of a Text Display Entity.", Types.McWIKI})
@Examples("set line width of {_display} to 100")
@Since("INSERT VERSION")
public class ExprTextDisplayLineWidth extends SimplePropertyExpression<Display, Integer> {

    static {
        register(ExprTextDisplayLineWidth.class, Integer.class, "line width", "displayentities");
    }

    @Override
    public @Nullable Integer convert(Display display) {
        if (display instanceof TextDisplay textDisplay) return textDisplay.getLineWidth();
        return null;
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
        if (delta != null && delta[0] instanceof Integer width) {
            for (Display display : getExpr().getArray(event)) {
                if (display instanceof TextDisplay textDisplay) {
                    textDisplay.setLineWidth(width);
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
        return "line width";
    }

}
