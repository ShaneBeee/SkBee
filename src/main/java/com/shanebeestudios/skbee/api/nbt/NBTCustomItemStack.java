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
    private final boolean useComponents;

    public NBTCustomItemStack(ItemStack itemStack, boolean useComponents) {
        super(getContainer(NBTItem.convertItemtoNBT(itemStack), useComponents).toString());
        this.originalItemStack = itemStack;
        this.useComponents = useComponents;
    }

    private static NBTCompound getContainer(NBTContainer itemContainer, boolean useComponents) {
        NBTCompound componentsContainer = itemContainer.getOrCreateCompound(NBTApi.TAG_NAME);
        if (useComponents) {
            return componentsContainer;
        } else {
            return componentsContainer.getOrCreateCompound("minecraft:custom_data");
        }
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        NBTContainer originalItemContainer = NBTItem.convertItemtoNBT(this.originalItemStack.clone());
        NBTCompound components = getContainer(originalItemContainer, this.useComponents);
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
