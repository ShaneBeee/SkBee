package com.shanebeestudios.skbee.elements.virtualfurnace.listener;

import com.shanebeestudios.vf.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class VirtualFurnaceListener implements Listener {

    private static final NamespacedKey KEY = Util.getKey("furnaceid");

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    private void onDropVirtualFurnace(ItemDespawnEvent event) {
        ItemStack itemStack = event.getEntity().getItemStack();
        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (container.has(KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
            event.getEntity().setUnlimitedLifetime(true);
        }
    }

}
