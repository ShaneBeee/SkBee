package com.shanebeestudios.skbee.api.NBT;

import ch.njol.skript.util.slot.Slot;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTItemSlot extends NBTItem {

    private final Slot slot;

    public NBTItemSlot(Slot slot) {
        super(slot.getItem() != null ? slot.getItem() : new ItemStack(Material.STONE), true);
        this.slot = slot;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        this.slot.setItem(getItem());
    }

}
