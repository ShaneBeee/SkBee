package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class for {@link NBTItem} using a Bukkit {@link ItemStack}
 * <p>This is used due to a deprecation in NBTItem, as well as a protected constructor</p>
 */
public class NBTCustomItemStack extends NBTItem {

    public NBTCustomItemStack(ItemStack item) {
        super(item, true, false, false);
    }

}
