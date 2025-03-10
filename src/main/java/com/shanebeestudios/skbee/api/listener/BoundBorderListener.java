package com.shanebeestudios.skbee.api.listener;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundEnterEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundExitEvent;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.region.scheduler.TaskUtils;
import com.shanebeestudios.skbee.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class BoundBorderListener implements Listener {

    private final BoundConfig boundConfig;

    // Bukkit 1.20.5 moved these events from Spigot package to Bukkit package
    private static final boolean HAS_MOUNT_EVENT = Skript.classExists("org.bukkit.event.entity.EntityMountEvent");
    private static final boolean HAS_DISMOUNT_EVENT = Skript.classExists("org.bukkit.event.entity.EntityDismountEvent");

    public BoundBorderListener(SkBee plugin) {
        Config config = plugin.getPluginConfig();
        this.boundConfig = plugin.getBoundConfig();
        setupListeners(plugin, config);
    }

    private void setupListeners(SkBee plugin, Config config) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (config.BOUND_EVENTS_PLAYER_MOVE) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
        }, plugin);

        if (config.BOUND_EVENTS_PLAYER_TELEPORT) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onPlayerTeleport(PlayerTeleportEvent event) {
                Player player = event.getPlayer();
                Location from = event.getFrom();
                Location to = event.getTo();
                if (preventBoundMovement(player, from, to, false)) {
                    event.setCancelled(true);
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_PLAYER_RESPAWN) pluginManager.registerEvents(new Listener() {
            @SuppressWarnings("deprecation")
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onRespawn(PlayerRespawnEvent event) {
                Player player = event.getPlayer();
                Location from = player.getLocation();
                Location to = event.getRespawnLocation();
                if (preventBoundMovement(player, from, to)) {
                    event.setRespawnLocation(Bukkit.getWorlds().getFirst().getSpawnLocation());
                    if (event.isBedSpawn() || event.isAnchorSpawn()) {
                        player.setBedSpawnLocation(null);
                    }
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_PLAYER_BED_ENTER) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onEnterBed(PlayerBedEnterEvent event) {
                Player player = event.getPlayer();
                Location from = player.getLocation();
                Location to = event.getBed().getLocation();
                if (preventBoundMovement(player, from, to)) {
                    event.setCancelled(true);
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_PLAYER_BED_LEAVE) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onExitBed(PlayerBedLeaveEvent event) {
                Player player = event.getPlayer();
                Location from = event.getBed().getLocation();
                TaskUtils.getEntityScheduler(player).runTaskLater(() -> {
                    // Find player's new location after leaving bed
                    // have to add a delay as this isn't determinded in the event
                    Location to = player.getLocation();
                    if (preventBoundMovement(player, from, to)) {
                        player.teleport(from.clone().add(0, 1, 0));
                    }
                }, 1);
            }
        }, plugin);

        if (config.BOUND_EVENTS_ENTITY_MOUNT && HAS_MOUNT_EVENT) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onMount(EntityMountEvent event) {
                if (event.getEntity() instanceof Player player) {
                    Location from = player.getLocation();
                    Location to = event.getMount().getLocation();
                    if (preventBoundMovement(player, from, to)) {
                        event.setCancelled(true);
                    }
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_ENTITY_DISMOUNT && HAS_DISMOUNT_EVENT) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onDismount(EntityDismountEvent event) {
                if (event.getEntity() instanceof Player player) {
                    Location from = event.getDismounted().getLocation().clone();
                    TaskUtils.getEntityScheduler(player).runTaskLater(() -> {
                        Location to = player.getLocation();
                        if (preventBoundMovement(player, from, to)) {
                            from.setYaw(player.getLocation().getYaw());
                            from.setPitch(player.getLocation().getPitch());
                            player.teleport(from);
                        }
                    }, 1);
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_VEHICLE_ENTER) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onVehicleEnter(VehicleEnterEvent event) {
                if (event.getEntered() instanceof Player player) {
                    Location from = player.getLocation();
                    Location to = event.getVehicle().getLocation();
                    if (preventBoundMovement(player, from, to)) {
                        event.setCancelled(true);
                    }
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_VEHICLE_EXIT) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onVehicleExit(VehicleExitEvent event) {
                Location from = event.getVehicle().getLocation().clone();
                if (event.getExited() instanceof Player player) {
                    TaskUtils.getEntityScheduler(player).runTaskLater(() -> {
                        Location to = player.getLocation();
                        if (preventBoundMovement(player, from, to)) {
                            from.setYaw(player.getLocation().getYaw());
                            from.setPitch(player.getLocation().getPitch());
                            player.teleport(from);
                        }
                    }, 1);
                }
            }
        }, plugin);

        if (config.BOUND_EVENTS_VEHICLE_MOVE) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onVehicleMove(VehicleMoveEvent event) {
                Vehicle vehicle = event.getVehicle();
                vehicle.getPassengers().forEach(entity -> {
                    if (entity instanceof Player player) {
                        Location from = event.getFrom().clone();
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
        }, plugin);

        if (config.BOUND_EVENTS_VEHICLE_DESTROY) pluginManager.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onVehicleDestroy(VehicleDestroyEvent event) {
                Vehicle vehicle = event.getVehicle();
                Location from = vehicle.getLocation().clone();
                TaskUtils.getEntityScheduler(vehicle).runTaskLater(() -> {
                    for (Entity passenger : vehicle.getPassengers()) {
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
        }, plugin);
    }

    private boolean preventBoundMovement(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        return preventBoundMovement(player, from, to, true);
    }

    private boolean preventBoundMovement(@NotNull Player player, @NotNull Location from, @NotNull Location to, boolean ignoreWorldChange) {
        // Clone to prevent changing event values
        from = from.clone();
        // Only detect body movement not head movement
        from.setPitch(to.getPitch());
        from.setYaw(to.getYaw());
        // Skip same location and different worlds
        if (to.equals(from)) return false;
        if (ignoreWorldChange && !to.getWorld().equals(from.getWorld())) return false;
        //Collection<Bound> bounds = ignoreWorldChange ? boundConfig.getBoundsInRegion(from) : boundConfig.getBounds();
        for (Bound bound : boundConfig.getBoundsInRegion(from)) {
            // Exit called first, as we'd probably leave one before entering another
            if (!bound.isInRegion(to) && bound.isInRegion(from)) {
                BoundExitEvent exitEvent = new BoundExitEvent(bound, player);
                Bukkit.getPluginManager().callEvent(exitEvent);
                if (exitEvent.isCancelled()) {
                    return true;
                }
            }

        }
        for (Bound bound : boundConfig.getBoundsInRegion(to)) {
            if (bound.isInRegion(to) && !bound.isInRegion(from)) {
                BoundEnterEvent enterEvent = new BoundEnterEvent(bound, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
                if (enterEvent.isCancelled()) {
                    return true;
                }
            }
        }
        return false;
    }

}
