package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.virtualfurnace.type.Types;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("VirtualFurnace - All Machines")
@Description("Get all registered machines.")
@Examples("set {_machines::*} to all virtual furnaces")
@Since("3.3.0")
public class ExprVirtualFurnaceAllFurnaces extends SimpleExpression<Machine> {

    static {
        Skript.registerExpression(ExprVirtualFurnaceAllFurnaces.class, Machine.class, ExpressionType.SIMPLE,
                "all virtual (machines|furnaces)");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Machine[] get(Event event) {
        List<Machine> machines = new ArrayList<>(Types.FURNACE_MANAGER.getAllFurnaces());
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
