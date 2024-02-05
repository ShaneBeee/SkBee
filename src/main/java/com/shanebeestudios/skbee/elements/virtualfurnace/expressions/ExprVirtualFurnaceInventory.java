package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.vf.api.machine.Furnace;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("VirtualFurnace - Inventory")
@Description("Get the inventory of a virtual furnace. Can be used to open for a player.")
@Examples({"set {_inv} to virtual furnace inventory of {_furnace}",
        "open {_inv} to player"})
@Since("INSERT VERSION")
public class ExprVirtualFurnaceInventory extends SimplePropertyExpression<Machine, Inventory> {

    static {
        register(ExprVirtualFurnaceInventory.class, Inventory.class,
                "[virtual] (machine|furnace) inventory", "machines");
    }

    @Override
    public @Nullable Inventory convert(Machine machine) {
        if (machine instanceof Furnace furnace) return furnace.getInventory();
        return null;
    }

    @Override
    public @NotNull Class<? extends Inventory> getReturnType() {
        return Inventory.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "machine inventory";
    }

}
