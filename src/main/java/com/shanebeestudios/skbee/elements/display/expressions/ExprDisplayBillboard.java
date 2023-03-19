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
import org.bukkit.entity.Display.Billboard;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Billboard")
@Description({"Represents the billboard of a Display Entity.", Types.McWIKI})
@Examples("set display billboard of {_display} to horizontal")
@Since("INSERT VERSION")
public class ExprDisplayBillboard extends SimplePropertyExpression<Display, Billboard> {

    static {
        register(ExprDisplayBillboard.class, Billboard.class, "display billboard", "displayentities");
    }

    @Override
    public @Nullable Billboard convert(Display display) {
        return display.getBillboard();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Billboard.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Billboard billboard) {
            for (Display display : getExpr().getArray(event)) {
                display.setBillboard(billboard);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Billboard> getReturnType() {
        return Billboard.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display billboard";
    }

}
