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

@Name("DisplayEntity - Text Default Background")
@Description({"Represents if a Text Display Entity should use a default background.", Types.McWIKI})
@Examples("set default text background of {_display} to true")
@Since("INSERT VERSION")
public class ExprTextDisplayDefaultBackground extends SimplePropertyExpression<Display, Boolean> {

    static {
        register(ExprTextDisplayDefaultBackground.class, Boolean.class,
                "default [text] background", "displayentities");
    }

    @Override
    public @Nullable Boolean convert(Display display) {
        if (display instanceof TextDisplay textDisplay) return textDisplay.isDefaultBackground();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "NullableProblems"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean isDefaultBackground) {
            for (Display display : getExpr().getArray(event)) {
                if (display instanceof TextDisplay textDisplay) {
                    textDisplay.setDefaultBackground(isDefaultBackground);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "default background";
    }

}
