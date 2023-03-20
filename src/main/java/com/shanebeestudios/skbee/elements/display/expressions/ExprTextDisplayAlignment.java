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
import org.bukkit.entity.TextDisplay.TextAligment;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Alignment")
@Description({"Represents the text alignment of a Text Display Entity.", Types.McWIKI})
@Examples("set text alignment of {_display} to aligned left")
@Since("INSERT VERSION")
public class ExprTextDisplayAlignment extends SimplePropertyExpression<Display, TextAligment> {

    static {
        register(ExprTextDisplayAlignment.class, TextAligment.class, "text alignment", "displayentities");
    }

    @Override
    public @Nullable TextAligment convert(Display display) {
        if (display instanceof TextDisplay textDisplay) return textDisplay.getAlignment();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(TextAligment.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof TextAligment textAligment) {
            for (Display display : getExpr().getArray(event)) {
                if (display instanceof TextDisplay textDisplay) {
                    textDisplay.setAlignment(textAligment);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends TextAligment> getReturnType() {
        return TextAligment.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "text alignment";
    }

}
