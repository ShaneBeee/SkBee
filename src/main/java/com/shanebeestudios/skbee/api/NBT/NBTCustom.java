package com.shanebeestudios.skbee.api.NBT;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.NamespacedKey;

public interface NBTCustom {

    NamespacedKey OLD_KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");

    void deleteCustomNBT();

}
