package tk.shanebee.bee.api.listener;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBT.NBTCustomBlock;
import tk.shanebee.bee.api.NBTApi;

public class NBTListener implements Listener {

    // Note regarding event priority:
    // We use EventPriority.MONITOR, to make sure any event
    // called in Skript is handled before we touch it
    // This way a user can retrieve the nbt before it's deleted

    private final SkBee plugin;

    public NBTListener(SkBee plugin) {
        this.plugin = plugin;
    }

    // If a player breaks a block with NBT, remove the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        breakBlock(event.getBlock());
    }

    // If an entity breaks a block with NBT, remove the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityBreakBlock(EntityChangeBlockEvent event) {
        if (event.isCancelled()) return;
        switch (event.getEntity().getType()) {
            case ENDERMAN: // pickup blocks
            case ZOMBIE: // break doors
            case SILVERFISH: // changes block when entering
            case RABBIT: // breaks carrots
            case RAVAGER: // tramples blocks
            case WITHER: // I dunno what they do
                breakBlock(event.getBlock());
        }
    }

    // If a block explodes, remove NBT from the exploded blocks
    @EventHandler(priority = EventPriority.MONITOR)
    private void onExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        event.blockList().forEach(this::breakBlock);
    }

    // If an entity explodes, remove NBT from the exploded blocks
    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        event.blockList().forEach(this::breakBlock);
    }

    private void breakBlock(Block block) {
        BlockState blockState = block.getState();
        if (NBTApi.isTileEntity(blockState)) return;
        if (!NBTApi.SUPPORTS_BLOCK_NBT) return;
        NBTCompound chunkContainer = new NBTChunk(block.getChunk()).getPersistentDataContainer();

        if (chunkContainer.hasKey("blocks")) {
            NBTCompound blocksContainer = chunkContainer.getCompound("blocks");
            String blockKey = getKey(block);
            if (blocksContainer.hasKey(blockKey)) {
                blocksContainer.removeKey(blockKey);
            }
        }
    }

    // If a piston moves a block with NBT, we shift the NBT
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPistonPush(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        Block piston = event.getBlock();
        BlockState blockState = piston.getState();

        if (event.isSticky()) return;
        if (NBTApi.isTileEntity(blockState)) return;
        if (!NBTApi.SUPPORTS_BLOCK_NBT) return;

        event.getBlocks().forEach(block -> {
            NBTCompound chunkContainer = new NBTChunk(block.getChunk()).getPersistentDataContainer();
            if (chunkContainer.hasKey("blocks")) {
                NBTCompound blocksContainer = chunkContainer.getCompound("blocks");
                if (blocksContainer.hasKey(getKey(block))) {
                    shiftBlock(block, blocksContainer, event.getDirection());
                }
            }
        });
    }

    private void shiftBlock(Block block, NBTCompound compound, BlockFace direction) {
        BlockData oldBlockData = block.getBlockData();
        Block newBlock = block.getRelative(direction, 1);
        NBTCompound oldData = new NBTCustomBlock(block).cloneCustomData();

        compound.removeKey(getKey(block));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (newBlock.getBlockData().matches(oldBlockData)) {
                new NBTCustomBlock(newBlock).getCustomData().mergeCompound(oldData);
            }
        }, 5);
    }

    private String getKey(Block block) {
        return String.format("%s_%s_%s", block.getX(), block.getY(), block.getZ());
    }

}
