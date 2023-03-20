package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Background Color")
@Description({"Represents the text background color of a Text Display Entity.",
        "NOTE: Due to Skript Color missing the alpha channel, you must use Bukkit Colors here.", Types.McWIKI})
@Examples("set text background color of {_display} to bukkitColor(55,100,0,150)")
@Since("2.8.0")
public class ExprTextDisplayBackgroundColor extends SimplePropertyExpression<Entity, Color> {

    static {
        register(ExprTextDisplayBackgroundColor.class, Color.class,
                "text background color", "entities");
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Color convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) return textDisplay.getBackgroundColor();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Color.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "deprecation", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Color color) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof TextDisplay textDisplay) {
                    textDisplay.setBackgroundColor(color);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "text background color";
    }

}
