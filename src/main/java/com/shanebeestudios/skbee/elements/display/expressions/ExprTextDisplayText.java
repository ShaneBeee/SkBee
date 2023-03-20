package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Display Text")
@Description({"Represents the display text of a Text Display Entity.",
        "NOTE: Supports multiple lines.", Types.McWIKI})
@Examples("set display text of {_display} to \"Line 1\", \"Line 2\" and \"Line 3\"")
@Since("INSERT VERSION")
public class ExprTextDisplayText extends SimplePropertyExpression<Entity, String> {

    static {
        register(ExprTextDisplayText.class, String.class, "display text", "entities");
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable String convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) return textDisplay.getText();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String[].class);
        return null;
    }

    @SuppressWarnings({"deprecation", "NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta == null) return;
        String text;
        if (delta.length == 1) {
            text = ((String) delta[0]);
        } else {
            text = StringUtils.join(delta, System.lineSeparator());
        }
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof TextDisplay textDisplay) {
                textDisplay.setText(text);
            }
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display text";
    }

}
