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
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Display Brightness")
@Description({"Represents the brightness attributes of a Display Entity.",
        "NOTE: Delete/reset will reset the Display Entity's brightness to match it's surroundings.",
        "NOTE: If this is not set in the first place, it will return nothing!", Types.McWIKI})
@Examples("set display brightness of {_display} to displayBrightness(10,10)")
@Since("2.8.0")
public class ExprDisplayBrightness extends SimplePropertyExpression<Entity, Brightness> {

    static {
        register(ExprDisplayBrightness.class, Brightness.class,
                "display brightness", "entities");
    }

    @Override
    public @Nullable Brightness convert(Entity entity) {
        if (entity instanceof Display display) return display.getBrightness();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Brightness.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Brightness brightness = (delta != null && delta[0] instanceof Brightness b) ? b : null;
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Display display) display.setBrightness(brightness);
        }
    }

    @Override
    public @NotNull Class<? extends Brightness> getReturnType() {
        return Brightness.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display brightness";
    }

}
