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
import com.shanebeestudios.vf.api.machine.Furnace;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Name("VirtualFurnace - Machine from ID")
@Description("Get a virtual furnace from a uuid.")
@Examples("set {_furnace} to virtual furnace from id {_uuid}")
@Since("3.3.0")
public class ExprVirtualFurnaceMachineFromID extends SimpleExpression<Machine> {

    static {
        Skript.registerExpression(ExprVirtualFurnaceMachineFromID.class, Machine.class, ExpressionType.COMBINED,
                "[virtual] (machine|furnace)[s] (from|with) (id|uuid)[s] %strings%");
    }

    private Expression<String> ids;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.ids = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Machine[] get(Event event) {
        List<Machine> machines = new ArrayList<>();
        for (String s : this.ids.getArray(event)) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(s);
            } catch (IllegalArgumentException ignore) {
            }
            if (uuid == null) continue;
            Machine machine = Types.FURNACE_MANAGER.getByID(uuid);
            machines.add(machine);
        }
        return machines.toArray(new Machine[0]);
    }

    @Override
    public boolean isSingle() {
        return this.ids.isSingle();
    }

    @Override
    public @NotNull Class<? extends Machine> getReturnType() {
        return Machine.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "virtual machine[s] from id[s] " + this.ids.toString(e, d);
    }

}
