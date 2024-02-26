package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.bukkit.block.Block;

/**
 * Represents the NBT of a non tile entity block
 */
public class NBTCustomBlock extends NBTContainer {

    private final Block block;
    private final String blockTag;
    private final String blockID;
    private final NBTCompound chunkData;
    private final boolean canSave;

    public NBTCustomBlock(Block block) {
        this.block = block;
        this.blockTag = String.format("%s_%s_%s", block.getX(), block.getY(), block.getZ());
        this.blockID = block.getType().getKey().toString();
        this.chunkData = new NBTChunk(block.getChunk()).getPersistentDataContainer();
        if (this.chunkData.hasTag("blocks")) {
            NBTCompound blocksCompound = this.chunkData.getOrCreateCompound("blocks");
            if (blocksCompound.hasTag(this.blockTag)) {
                NBTCompound blockCompound = blocksCompound.getOrCreateCompound(this.blockTag);
                this.mergeCompound(blockCompound);
            }
        }
        this.canSave = true;
    }

    @Override
    protected void saveCompound() {
        // Skip saving when we're loading the NBT in the constructor
        if (!canSave) return;
        if (getKeys().isEmpty()) {
            if (this.chunkData.hasTag("blocks", NBTType.NBTTagCompound)) {
                NBTCompound blocksCompound = this.chunkData.getOrCreateCompound("blocks");
                blocksCompound.removeKey(this.blockTag);
                if (blocksCompound.getKeys().isEmpty()) {
                    this.chunkData.removeKey("blocks");
                }
            }
            return;
        }
        NBTCompound blockCompound = this.chunkData.getOrCreateCompound("blocks").getOrCreateCompound(this.blockTag);
        blockCompound.mergeCompound(this);
    }

    @Override
    public String toString() {
        NBTContainer tag = new NBTContainer();
        tag.mergeCompound(this);
        tag.setString("id", this.blockID);
        tag.setInteger("x", this.block.getX());
        tag.setInteger("y", this.block.getY());
        tag.setInteger("z", this.block.getZ());
        return tag.toString();
    }

}
