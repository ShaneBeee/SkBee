package tk.shanebee.bee.api.NBT;

import ch.njol.skript.aliases.ItemType;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class NBTItemType extends NBTItem {

    private final ItemType itemType;

    public NBTItemType(ItemType itemType) {
        super(itemType.getRandom(), true);
        this.itemType = itemType;
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        this.itemType.setItemMeta(getItem().getItemMeta());
    }

}
