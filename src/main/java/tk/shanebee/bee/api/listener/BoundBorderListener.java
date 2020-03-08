package tk.shanebee.bee.api.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.event.EnterBoundEvent;
import tk.shanebee.bee.api.event.ExitBoundEvent;
import tk.shanebee.bee.elements.bound.config.BoundConfig;
import tk.shanebee.bee.elements.bound.objects.Bound;

public class BoundBorderListener implements Listener {

    private final BoundConfig boundConfig;

    public BoundBorderListener(SkBee plugin) {
        this.boundConfig = plugin.getBoundConfig();
    }

    @EventHandler
    private void onBoundBorder(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || to.equals(from)) {
            return;
        }
        for (Bound bound : boundConfig.getBounds()) {
            if (bound.isInRegion(to) && !bound.isInRegion(from)) {
                EnterBoundEvent enterEvent = new EnterBoundEvent(bound, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
                if (enterEvent.isCancelled()) {
                    player.teleport(from);
                }
            }
            if (!bound.isInRegion(to) && bound.isInRegion(from)) {
                ExitBoundEvent exitEvent = new ExitBoundEvent(bound, player);
                Bukkit.getPluginManager().callEvent(exitEvent);
                if (exitEvent.isCancelled()) {
                    player.teleport(from);
                }
            }
        }
    }

}
