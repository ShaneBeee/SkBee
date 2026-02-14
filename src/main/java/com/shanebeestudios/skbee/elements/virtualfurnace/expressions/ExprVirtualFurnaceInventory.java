package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.vf.api.machine.Furnace;
import com.shanebeestudios.vf.api.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprVirtualFurnaceInventory extends SimplePropertyExpression<Machine, Inventory> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprVirtualFurnaceInventory.class, Inventory.class,
                "[virtual] (machine|furnace) inventory", "machines")
            .name("VirtualFurnace - Inventory")
            .description("Get the inventory of a virtual furnace. Can be used to open for a player.")
            .examples("set {_inv} to virtual furnace inventory of {_furnace}",
                "open {_inv} to player")
            .since("3.3.0")
            .register();
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
