package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for {@link NBTItem} using a Bukkit {@link ItemStack}
 * <p>This is used due to a deprecation in NBTItem, as well as a protected constructor,
 * as well as funny changes in NBT-API due to MC 1.20.5</p>
 */
@SuppressWarnings("deprecation")
public class NBTCustomItemStack extends NBTContainer implements NBTCustom {

    private final ItemStack originalItemStack;
    private final boolean isFull;

    public NBTCustomItemStack(ItemStack itemStack, boolean isVanilla, boolean isFull) {
        super(getInitialContainer(itemStack, isVanilla, isFull).getCompound());
        this.originalItemStack = itemStack;
        this.isFull = isFull;
    }

    private static NBTCompound getInitialContainer(ItemStack itemStack, boolean isVanilla, boolean isFull) {
        NBTCompound nbtContainer;
        if (isVanilla) {
            nbtContainer = NBTReflection.getVanillaNBT(itemStack);
        } else {
            nbtContainer = NBTItem.convertItemtoNBT(itemStack);
        }
        if (nbtContainer == null) nbtContainer = new NBTContainer();
        // Create a clone (Minecraft seems to freak out without doing this)
        NBTCompound clone = new NBTContainer();
        clone.mergeCompound(getContainer(nbtContainer, isFull));
        return clone;
    }

    private static NBTCompound getContainer(NBTCompound itemContainer, boolean isFull) {
        if (isFull) {
            // TODO temp solution until NBT API handles this
            // DataVersion is used for deserializing and running thru DataFixerUpper
            //itemContainer.setInteger("DataVersion", NBTApi.getDataVersion());
            // TODO end
            return itemContainer;
        }
        return itemContainer.getOrCreateCompound(NBTApi.TAG_NAME);
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        if (this.isFull) return;
        NBTContainer originalItemContainer = NBTItem.convertItemtoNBT(this.originalItemStack.clone());
        NBTCompound components = getContainer(originalItemContainer, false);
        components.clearNBT();
        components.mergeCompound(this);
        ItemStack itemStack = NBTItem.convertNBTtoItem(originalItemContainer);
        if (itemStack == null) return;
        this.originalItemStack.setItemMeta(itemStack.getItemMeta());
    }

    public ItemStack getItem() {
        return this.originalItemStack.clone();
    }

    @Override
    public void deleteCustomNBT() {
        // Unused
    }

    @Override
    public @NotNull NBTCompound getCopy() {
        NBTContainer nbtContainer = new NBTContainer();
        nbtContainer.mergeCompound(this);
        return nbtContainer;
    }

    @Override
    public @NotNull NBTCompound getCustomNBT() {
        if (NBTApi.HAS_ITEM_COMPONENTS) {
            return this.getOrCreateCompound("minecraft:custom_data");
        }
        // Prior to 1.20.5, the "tag" compound could store any custom data
        // After 1.20.5 this is merged into the "minecraft:custom_data" component
        return this;
    }

}
