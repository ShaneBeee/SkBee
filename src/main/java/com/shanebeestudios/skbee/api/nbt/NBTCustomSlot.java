package com.shanebeestudios.skbee.api.nbt;

import ch.njol.skript.util.slot.Slot;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Wrapper class for {@link NBTItem} using a Skript {@link Slot}
 */
public class NBTCustomSlot extends NBTItem {

    private final Slot slot;

    public NBTCustomSlot(Slot slot) {
        super(Objects.requireNonNull(slot.getItem()), true);
        this.slot = slot;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        ItemStack item = this.slot.getItem();
        if (item == null) return;
        item.setItemMeta(getItem().getItemMeta());
    }

}
