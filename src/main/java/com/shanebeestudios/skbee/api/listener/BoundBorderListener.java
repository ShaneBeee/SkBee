package com.shanebeestudios.skbee.api.listener;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.event.bound.BoundEnterEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundExitEvent;
import com.shanebeestudios.skbee.config.BoundConfig;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class BoundBorderListener implements Listener {

    private final SkBee plugin;
    private final BoundConfig boundConfig;
    private boolean PLAYER_MOVE;
    private boolean PLAYER_TELEPORT;
    private boolean PLAYER_BED_LEAVE;
    private boolean PLAYER_BED_ENTER;
    private boolean PLAYER_RESPAWN;
    private boolean ENTITY_MOUNT;
    private boolean ENTITY_DISMOUNT;
    private boolean VEHICLE_MOVE;
    private boolean VEHICLE_DESTROY;
    private boolean VEHICLE_EXIT;
    private boolean VEHICLE_ENTER;

    public BoundBorderListener(SkBee plugin) {
        Config config = plugin.getPluginConfig();
        this.plugin = plugin;
        this.boundConfig = plugin.getBoundConfig();
        this.PLAYER_MOVE = config.BOUND_EVENTS_PLAYER_MOVE;
        this.PLAYER_TELEPORT = config.BOUND_EVENTS_PLAYER_TELEPORT;
        this.PLAYER_RESPAWN = config.BOUND_EVENTS_PLAYER_RESPAWN;
        this.PLAYER_BED_ENTER = config.BOUND_EVENTS_PLAYER_BED_ENTER;
        this.PLAYER_BED_LEAVE = config.BOUND_EVENTS_PLAYER_BED_LEAVE;
        this.ENTITY_MOUNT = config.BOUND_EVENTS_ENTITY_MOUNT;
        this.ENTITY_DISMOUNT = config.BOUND_EVENTS_ENTITY_DISMOUNT;
        this.VEHICLE_ENTER = config.BOUND_EVENTS_VEHICLE_ENTER;
        this.VEHICLE_EXIT = config.BOUND_EVENTS_VEHICLE_EXIT;
        this.VEHICLE_MOVE = config.BOUND_EVENTS_VEHICLE_MOVE;
        this.VEHICLE_DESTROY = config.BOUND_EVENTS_VEHICLE_DESTROY;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onBoundBorder(PlayerMoveEvent event) {
        if (!this.PLAYER_MOVE) return;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!this.PLAYER_TELEPORT) return;
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (preventBoundMovement(player, from, to)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onRespawn(PlayerRespawnEvent event) {
        if (!this.PLAYER_RESPAWN) return;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEnterBed(PlayerBedEnterEvent event) {
        if (!this.PLAYER_BED_ENTER) return;
        Player player = event.getPlayer();
        Location from = player.getLocation();
        Location to = event.getBed().getLocation();
        if (preventBoundMovement(player, from, to)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onExitBed(PlayerBedLeaveEvent event) {
        if (!this.PLAYER_BED_LEAVE) return;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onMount(EntityMountEvent event) {
        if (!this.ENTITY_MOUNT) return;
        if (event.getEntity() instanceof Player player) {
            Location from = player.getLocation();
            Location to = event.getMount().getLocation();
            if (preventBoundMovement(player, from, to)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onDismount(EntityDismountEvent event) {
        if (!this.ENTITY_DISMOUNT) return;
        if (event.getEntity() instanceof Player player) {
            Location from = event.getDismounted().getLocation();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location to = player.getLocation();
                if (preventBoundMovement(player, from, to)) {
                    from.setYaw(player.getLocation().getYaw());
                    from.setPitch(player.getLocation().getPitch());
                    player.teleport(from);
                }
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVehicleEnter(VehicleEnterEvent event) {
        if (!this.VEHICLE_ENTER) return;
        if (event.getEntered() instanceof Player player) {
            Location from = player.getLocation();
            Location to = event.getVehicle().getLocation();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVehicleExit(VehicleExitEvent event) {
        if (!this.VEHICLE_EXIT) return;
        Location from = event.getVehicle().getLocation();
        if (event.getExited() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location to = player.getLocation();
                if (preventBoundMovement(player, from, to)) {
                    from.setYaw(player.getLocation().getYaw());
                    from.setPitch(player.getLocation().getPitch());
                    player.teleport(from);
                }
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVehicleMove(VehicleMoveEvent event) {
        if (!this.VEHICLE_MOVE) return;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!this.VEHICLE_DESTROY) return;
        Location from = event.getVehicle().getLocation();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity passenger : event.getVehicle().getPassengers()) {
                Location to = passenger.getLocation();
                if (passenger instanceof Player player) {
                    if (preventBoundMovement(player, from, to)) {
                        from.setYaw(player.getLocation().getYaw());
                        from.setPitch(player.getLocation().getPitch());
                        player.teleport(from);
                    }
                }
            }
        }, 1);
    }

    private boolean preventBoundMovement(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        // Only detect body movement not head movement
        from.setPitch(to.getPitch());
        from.setYaw(to.getYaw());
        // Skip same location and different worlds
        if (to.equals(from)) return false;
        if (!to.getWorld().equals(from.getWorld())) return false;
        for (Bound bound : boundConfig.getBoundsIn(from.getWorld())) {
            if (bound.isInRegion(to) && !bound.isInRegion(from)) {
                BoundEnterEvent enterEvent = new BoundEnterEvent(bound, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
                if (enterEvent.isCancelled()) {
                    return true;
                }
            }
            if (!bound.isInRegion(to) && bound.isInRegion(from)) {
                BoundExitEvent exitEvent = new BoundExitEvent(bound, player);
                Bukkit.getPluginManager().callEvent(exitEvent);
                if (exitEvent.isCancelled()) {
                    return true;
                }
            }
        }
        return false;
    }

}
