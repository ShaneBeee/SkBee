package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.vf.api.machine.Machine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("VirtualFurnace - UUID")
@Description("Get the id/uuid of a virtual furnace.")
@Examples("set {_uuid} to machine uuid of {_furnace}")
@Since("3.3.0")
public class ExprVirtualFurnaceMachineID extends SimplePropertyExpression<Machine, String> {

    static {
        register(ExprVirtualFurnaceMachineID.class, String.class,
                "[virtual] (machine|furnace) (id|uuid)", "machines");
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
