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

@Name("DisplayEntity - Text Opacity")
@Description({"Represents the text opacity of a Text Display Entity.", Types.McWIKI})
@Examples("set text opacity of {_display} to 50")
@Since("INSERT VERSION")
public class ExprTextDisplayOpacity extends SimplePropertyExpression<Display, Integer> {

    static {
        register(ExprTextDisplayOpacity.class, Integer.class, "text opacity", "displayentities");
    }

    @Override
    public @Nullable Integer convert(Display display) {
        if (display instanceof TextDisplay textDisplay) return (int) textDisplay.getTextOpacity();
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
        if (delta != null && delta[0] instanceof Integer integer) {
            byte opacity = ((Number) integer).byteValue();
            for (Display display : getExpr().getArray(event)) {
                if (display instanceof TextDisplay textDisplay) {
                    textDisplay.setTextOpacity(opacity);
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
        return "text opacity";
    }

}
