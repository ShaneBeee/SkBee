package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Alignment")
@Description({"Represents the text alignment of a Text Display Entity.", Types.McWIKI})
@Examples("set display text alignment of {_display} to left aligned")
@Since("2.8.0")
public class ExprTextDisplayAlignment extends SimplePropertyExpression<Entity, TextAlignment> {

    static {
        register(ExprTextDisplayAlignment.class, TextAlignment.class,
                "[display] text alignment", "entities");
    }

    @Override
    public @Nullable TextAlignment convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) return textDisplay.getAlignment();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(TextAlignment.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof TextAlignment textAligment) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof TextDisplay textDisplay) {
                    textDisplay.setAlignment(textAligment);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends TextAlignment> getReturnType() {
        return TextAlignment.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "text alignment";
    }

}
