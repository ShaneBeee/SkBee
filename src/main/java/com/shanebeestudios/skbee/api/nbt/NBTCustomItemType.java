package com.shanebeestudios.skbee.api.nbt;

import ch.njol.skript.aliases.ItemType;
import de.tr7zw.changeme.nbtapi.NBTItem;

/**
 * Wrapper class for {@link NBTItem} using a Skript {@link ItemType}
 */
public class NBTCustomItemType extends NBTCustomItemStack {

    private final ItemType itemType;

    public NBTCustomItemType(ItemType itemType, boolean isVanilla, boolean isFull) {
        super(itemType.getRandom(), isVanilla, isFull);
        this.itemType = itemType;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        this.itemType.setItemMeta(getItem().getItemMeta());
    }

}
