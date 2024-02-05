package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("VirtualFurnace - Machine Name")
@Description("Get/set the name of a virtual furnace.")
@Examples({"set {_name} to machine name of {_furnace}",
        "set machine name of {_furnace} to \"Super Fast Furnace\""})
@Since("INSERT VERSION")
public class ExprVirtualFurnaceMachineName extends SimplePropertyExpression<Machine, String> {

    static {
        register(ExprVirtualFurnaceMachineName.class, String.class,
                "[virtual] (machine|furnace) name", "machines");
    }

    @Override
    public @Nullable String convert(Machine machine) {
        return machine.getName();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] instanceof String string) {
            for (Machine machine : getExpr().getArray(event)) {
                machine.setName(string);
            }
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "machine name";
    }

}
