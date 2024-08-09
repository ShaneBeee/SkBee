package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class for {@link NBTItem} using a Bukkit {@link ItemStack}
 * <p>This is used due to a deprecation in NBTItem, as well as a protected constructor,
 * as well as funny changes in NBT-API due to MC 1.20.5</p>
 */
@SuppressWarnings("deprecation")
public class NBTCustomItemStack extends NBTContainer {

    private final ItemStack originalItemStack;
    private final boolean isCustomData;

    public NBTCustomItemStack(ItemStack itemStack, boolean isCustomData, boolean isVanilla, boolean isFull) {
        super(getInitialContainer(itemStack, isCustomData, isVanilla, isFull).toString());
        this.originalItemStack = itemStack;
        this.isCustomData = isCustomData;
    }

    private static NBTCompound getInitialContainer(ItemStack itemStack, boolean isCustomData, boolean isVanilla, boolean isFull) {
        NBTCompound nbtContainer;
        if (isVanilla) {
            nbtContainer = NBTReflection.getVanillaNBT(itemStack);
        } else {
            nbtContainer = NBTItem.convertItemtoNBT(itemStack);
        }
        if (nbtContainer == null) nbtContainer = new NBTContainer();
        return getContainer(nbtContainer, isCustomData, isFull);
    }

    private static NBTCompound getContainer(NBTCompound itemContainer, boolean isCustomData, boolean isFull) {
        if (isFull) return itemContainer;
        NBTCompound componentsContainer = itemContainer.getOrCreateCompound(NBTApi.TAG_NAME);
        if (isCustomData) {
            return componentsContainer.getOrCreateCompound("minecraft:custom_data");
        } else {
            return componentsContainer;
        }
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        NBTContainer originalItemContainer = NBTItem.convertItemtoNBT(this.originalItemStack.clone());
        NBTCompound components = getContainer(originalItemContainer, this.isCustomData, false);
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
