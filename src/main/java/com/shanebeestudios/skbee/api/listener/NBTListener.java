package com.shanebeestudios.skbee.api.listener;

import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.config.Config;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class NBTListener implements Listener {

    private final boolean BREAK_BLOCK;
    private final boolean PISTON_EXTEND;
    private final boolean ENTITY_CHANGE;
    private final boolean ENTITY_EXPLODE;
    private final boolean BLOCK_EXPLODE;

    public NBTListener(Config config) {
        this.BREAK_BLOCK = config.NBT_EVENTS_BREAK_BLOCK;
        this.PISTON_EXTEND = config.NBT_EVENTS_PISTON_EXTEND;
        this.ENTITY_CHANGE = config.NBT_EVENTS_ENTITY_CHANGE_BLOCK;
        this.ENTITY_EXPLODE = config.NBT_EVENTS_ENTITY_EXPLODE;
        this.BLOCK_EXPLODE = config.NBT_EVENTS_BLOCK_EXPLODE;
    }

    // Note regarding event priority:
    // We use EventPriority.MONITOR, to make sure any event
    // called in Skript is handled before we touch it
    // This way a user can retrieve the nbt before it's deleted

    // If a player breaks a block with NBT, remove the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent event) {
        if (!this.BREAK_BLOCK) return;
        if (event.isCancelled()) return;
        breakBlock(event.getBlock());
    }

    // If an entity breaks a block with NBT, remove the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityBreakBlock(EntityChangeBlockEvent event) {
        if (!this.ENTITY_CHANGE) return;
        if (event.isCancelled()) return;
        switch (event.getEntity().getType()) {
            // enderman = pickup blocks
            // zombie = break doors
            // silverfish = changes block when entering
            // rabbit = breaks carrots
            // ravager = tramples blocks
            // wither = I dunno what they do
            case ENDERMAN, ZOMBIE, SILVERFISH, RABBIT, RAVAGER, WITHER -> breakBlock(event.getBlock());
        }
    }

    // If a block explodes, remove NBT from the exploded blocks
    @EventHandler(priority = EventPriority.MONITOR)
    private void onExplode(BlockExplodeEvent event) {
        if (!this.BLOCK_EXPLODE) return;
        if (event.isCancelled()) return;
        event.blockList().forEach(this::breakBlock);
    }

    // If an entity explodes, remove NBT from the exploded blocks
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityExplode(EntityExplodeEvent event) {
        if (!this.ENTITY_EXPLODE) return;
        if (event.isCancelled()) return;
        event.blockList().forEach(this::breakBlock);
    }

    // If a piston moves a block with NBT, we remove the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPistonPush(BlockPistonExtendEvent event) {
        if (!this.PISTON_EXTEND) return;
        if (event.isCancelled()) return;
        Block piston = event.getBlock();
        BlockState blockState = piston.getState();

        if (event.isSticky()) return;
        if (blockState instanceof TileState) return;
        event.getBlocks().forEach(this::breakBlock);
    }

    private void breakBlock(Block block) {
        if (block.getState() instanceof TileState) return;

        NBTCompound chunkContainer = new NBTChunk(block.getChunk()).getPersistentDataContainer();

        if (chunkContainer.hasTag("blocks")) {
            NBTCompound blocksContainer = chunkContainer.getOrCreateCompound("blocks");
            removeNBT(blocksContainer, block);

            // Process joined blocks (ie: doors)
            if (block.getBlockData() instanceof Bisected bisected) {
                BlockFace face = bisected.getHalf() == Bisected.Half.BOTTOM ? BlockFace.UP : BlockFace.DOWN;
                removeNBT(blocksContainer, block.getRelative(face));
            }
            if (blocksContainer.getKeys().isEmpty()) chunkContainer.removeKey("blocks");
        }
    }

    private void removeNBT(NBTCompound blocksContainer, Block block) {
        String blockKey = String.format("%s_%s_%s", block.getX(), block.getY(), block.getZ());
        if (blocksContainer.hasTag(blockKey)) {
            blocksContainer.removeKey(blockKey);
        }
    }

}
