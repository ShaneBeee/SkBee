package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class for {@link NBTItem} using a Bukkit {@link ItemStack}
 * <p>This is used due to a deprecation in NBTItem, as well as a protected constructor</p>
 */
public class NBTCustomItemStack extends NBTContainer {

    private final ItemStack originalItemStack;

    public NBTCustomItemStack(ItemStack itemStack) {
        super(NBTItem.convertItemtoNBT(itemStack.clone()).getOrCreateCompound(NBTApi.TAG_NAME).toString());
        this.originalItemStack = itemStack;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        NBTContainer originalItemContainer = NBTItem.convertItemtoNBT(this.originalItemStack.clone());
        NBTCompound components = originalItemContainer.getOrCreateCompound(NBTApi.TAG_NAME);
        components.clearNBT();
        components.mergeCompound(this);
        ItemStack itemStack = NBTItem.convertNBTtoItem(originalItemContainer);
        if (itemStack == null) return;
        this.originalItemStack.setItemMeta(itemStack.getItemMeta());
    }

    public ItemStack getItem() {
        return this.originalItemStack.clone();
    }

}
