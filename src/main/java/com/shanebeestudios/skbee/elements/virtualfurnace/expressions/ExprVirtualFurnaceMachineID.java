package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.vf.api.machine.Machine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprVirtualFurnaceMachineID extends SimplePropertyExpression<Machine, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprVirtualFurnaceMachineID.class, String.class,
                "[virtual] (machine|furnace) (id|uuid)", "machines")
            .name("VirtualFurnace - UUID")
            .description("Get the id/uuid of a virtual furnace.")
            .examples("set {_uuid} to machine uuid of {_furnace}")
            .since("3.3.0")
            .register();
    }

    @Override
    public @Nullable String convert(Machine machine) {
        return machine.getUniqueID().toString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "machine id";
    }

}
