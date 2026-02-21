package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprVirtualFurnaceMachineName extends SimplePropertyExpression<Machine, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprVirtualFurnaceMachineName.class, String.class,
                "[virtual] (machine|furnace) name", "machines")
            .name("VirtualFurnace - Machine Name")
            .description("Get/set the name of a virtual furnace.")
            .examples("set {_name} to machine name of {_furnace}",
                "set machine name of {_furnace} to \"Super Fast Furnace\"")
            .since("3.3.0")
            .register();
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
