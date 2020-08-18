package tk.shanebee.bee.api.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import tk.shanebee.bee.api.event.EntityBlockInteractEvent;

public class EntityListener implements Listener {

    @EventHandler
    private void onEntityTrample(EntityInteractEvent event) {

        EntityBlockInteractEvent entityBlockInteractEvent = new EntityBlockInteractEvent(event.getEntity(), event.getBlock());
        Bukkit.getPluginManager().callEvent(entityBlockInteractEvent);

        event.setCancelled(entityBlockInteractEvent.isCancelled());
    }

    @EventHandler
    private void onPlayerTrample(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;

        EntityBlockInteractEvent entityBlockInteractEvent = new EntityBlockInteractEvent(event.getPlayer(), event.getClickedBlock());
        Bukkit.getPluginManager().callEvent(entityBlockInteractEvent);

        event.setCancelled(entityBlockInteractEvent.isCancelled());
    }

}
