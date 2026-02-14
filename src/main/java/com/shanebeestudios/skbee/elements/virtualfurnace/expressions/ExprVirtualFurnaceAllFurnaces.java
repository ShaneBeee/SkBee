package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.virtualfurnace.type.Types;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprVirtualFurnaceAllFurnaces extends SimpleExpression<Machine> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprVirtualFurnaceAllFurnaces.class, Machine.class,
                "all virtual (machines|furnaces)")
            .name("VirtualFurnace - All Machines")
            .description("Get all registered machines.")
            .examples("set {_machines::*} to all virtual furnaces")
            .since("3.3.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Machine[] get(Event event) {
        List<Machine> machines = new ArrayList<>(Types.FURNACE_MANAGER.getAllMachines());
        return machines.toArray(new Machine[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Machine> getReturnType() {
        return Machine.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "all virtual machines";
    }

}
