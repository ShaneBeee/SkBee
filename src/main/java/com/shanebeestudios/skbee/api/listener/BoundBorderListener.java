package com.shanebeestudios.skbee.api.listener;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.event.EnterBoundEvent;
import com.shanebeestudios.skbee.api.event.ExitBoundEvent;
import com.shanebeestudios.skbee.elements.bound.config.BoundConfig;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

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
        if (preventBoundMovement(player, from, to)) {
            event.setCancelled(true);
            Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                vehicle.removePassenger(player);
                vehicle.teleport(from);
            }
        }
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (preventBoundMovement(player, from, to)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            Location from = player.getLocation();
            Location to = event.getMount().getLocation();
            if (preventBoundMovement(player, from, to)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean preventBoundMovement(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        // Only detect body movement not head movement
        from.setPitch(to.getPitch());
        from.setYaw(to.getYaw());
        if (to.equals(from)) return false;

        for (Bound bound : boundConfig.getBounds()) {
            if (bound.isInRegion(to) && !bound.isInRegion(from)) {
                EnterBoundEvent enterEvent = new EnterBoundEvent(bound, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
                if (enterEvent.isCancelled()) {
                    return true;
                }
            }
            if (!bound.isInRegion(to) && bound.isInRegion(from)) {
                ExitBoundEvent exitEvent = new ExitBoundEvent(bound, player);
                Bukkit.getPluginManager().callEvent(exitEvent);
                if (exitEvent.isCancelled()) {
                    return true;
                }
            }
        }
        return false;
    }

}
