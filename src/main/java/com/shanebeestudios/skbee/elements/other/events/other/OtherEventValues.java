package com.shanebeestudios.skbee.elements.other.events.other;

import com.github.shanebeee.skr.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OtherEventValues {

    public static void register(Registration reg) {
        reg.newEventValue(SpawnerSpawnEvent.class, Block.class)
            .converter(event -> {
                CreatureSpawner spawner = event.getSpawner();
                if (spawner == null) return null;
                return spawner.getBlock();
            })
            .register();

        // Click Events
        reg.newEventValue(PlayerInteractEvent.class, BlockFace.class)
            .converter(PlayerInteractEvent::getBlockFace)
            .register();

        // Projectile Hit Event
        reg.newEventValue(ProjectileHitEvent.class, BlockFace.class)
            .converter(ProjectileHitEvent::getHitBlockFace)
            .register();

        reg.newEventValue(BlockPlaceEvent.class, BlockFace.class)
            .converter(event -> {
                Block placed = event.getBlockPlaced();
                Block against = event.getBlockAgainst();
                return against.getFace(placed);
            })
            .register();
    }

}
