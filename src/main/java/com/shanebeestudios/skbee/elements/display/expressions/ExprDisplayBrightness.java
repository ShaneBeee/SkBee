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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Display Brightness")
@Description({"Represents the brightness attributes of a Display Entity.",
        "NOTE: If this is not set in the first place, it will return nothing!", Types.McWIKI})
@Examples("set display brightness of {_display} to displayBrightness(10,10)")
@Since("INSERT VERSION")
public class ExprDisplayBrightness extends SimplePropertyExpression<Display, Brightness> {

    static {
        register(ExprDisplayBrightness.class, Brightness.class,
                "display brightness", "displayentities");
    }

    @Override
    public @Nullable Brightness convert(Display display) {
        return display.getBrightness();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Brightness.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Brightness brightness) {
            for (Display display : getExpr().getArray(event)) {
                display.setBrightness(brightness);
            }
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
