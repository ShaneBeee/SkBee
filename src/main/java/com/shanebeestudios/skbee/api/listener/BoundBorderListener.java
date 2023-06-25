package com.shanebeestudios.skbee.api.listener;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.event.EnterBoundEvent;
import com.shanebeestudios.skbee.api.event.ExitBoundEvent;
import com.shanebeestudios.skbee.config.BoundConfig;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

public class BoundBorderListener implements Listener {

    private final SkBee plugin;
    private final BoundConfig boundConfig;

    public BoundBorderListener(SkBee plugin) {
        this.plugin = plugin;
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

    @EventHandler
    private void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player) {
            Location from = player.getLocation();
            Location to = event.getVehicle().getLocation();
            if (preventBoundMovement(player, from, to)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        vehicle.getPassengers().forEach(entity -> {
            if (entity instanceof Player player) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (preventBoundMovement(player, from, to)) {
                    vehicle.removePassenger(player);
                    // Keep the player looking the same direction
                    from.setYaw(player.getLocation().getYaw());
                    from.setPitch(player.getLocation().getPitch());
                    player.teleport(from);
                }
            }
        });
    }

    @EventHandler
    private void onEnterBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Location from = player.getLocation();
        Location to = event.getBed().getLocation();
        if (preventBoundMovement(player, from, to)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExitBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getBed().getLocation();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            // Find player's new location after leaving bed
            // have to add a delay as this isn't determinded in the event
            Location to = player.getLocation();
            if (preventBoundMovement(player, from, to)) {
                player.teleport(from.add(0, 1, 0));
            }
        }, 1);

    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location from = player.getLocation();
        Location to = event.getRespawnLocation();
        if (preventBoundMovement(player, from, to)) {
            event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
            if (event.isBedSpawn() || event.isAnchorSpawn()) {
                player.setBedSpawnLocation(null);
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
