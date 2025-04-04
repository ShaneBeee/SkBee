package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the NBT of a non tile entity block
 */
public class NBTCustomBlock extends NBTContainer implements NBTCustom {

    private final Block block;
    private final String blockTag;
    private final String blockID;
    private final NBTCompound chunkData;
    private boolean canSave;

    @SuppressWarnings("deprecation")
    public NBTCustomBlock(Block block) {
        this.block = block;
        this.blockTag = String.format("%s_%s_%s", block.getX(), block.getY(), block.getZ());
        this.blockID = block.getType().getKey().toString();
        this.chunkData = new NBTChunk(block.getChunk()).getPersistentDataContainer();
        if (this.chunkData.hasTag("blocks", NBTType.NBTTagCompound)) {
            NBTCompound blocksInChunk = this.chunkData.getOrCreateCompound("blocks");
            if (this.block.getType().isAir()) {
                // If the block is air, let's clear any saved data
                this.canSave = true;
                blocksInChunk.removeKey(this.blockTag);
                if (blocksInChunk.getKeys().isEmpty()) {
                    this.chunkData.removeKey("blocks");
                }
            } else {
                if (blocksInChunk.hasTag(this.blockTag)) {
                    NBTCompound blockCompound = blocksInChunk.getOrCreateCompound(this.blockTag);
                    this.mergeCompound(blockCompound);
                }
            }
        }
        this.canSave = true;
    }

    @Override
    protected void saveCompound() {
        // Skip saving when we're loading the NBT in the constructor
        if (!this.canSave) return;
        if (this.block.getType().isAir()) {
            // If the block is air, let's clear out anything stored
            clearNBT();
        }
        if (getKeys().isEmpty()) {
            // If this compound is empty, let's clear empty compounds from the chunk
            if (this.chunkData.hasTag("blocks", NBTType.NBTTagCompound)) {
                NBTCompound blocksInChunk = this.chunkData.getOrCreateCompound("blocks");
                blocksInChunk.removeKey(this.blockTag);
                if (blocksInChunk.getKeys().isEmpty()) {
                    this.chunkData.removeKey("blocks");
                }
            }
            return;
        }
        NBTCompound blocksInChunk = this.chunkData.getOrCreateCompound("blocks");
        NBTCompound blockCompound = blocksInChunk.getOrCreateCompound(this.blockTag);
        blockCompound.clearNBT();
        blockCompound.mergeCompound(this);
    }

    @SuppressWarnings("deprecation")
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

    @Override
    public void deleteCustomNBT() {
        this.removeKey("custom");
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull NBTCompound getCopy() {
        NBTContainer emptyContainer = new NBTContainer();
        emptyContainer.mergeCompound(this);
        return emptyContainer;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull NBTCompound getCustomNBT() {
        NBTCompound custom = this.getOrCreateCompound("custom");
        // Verify the innards haven't been removed
        // If the block is air, the NBT is removed
        if (hasTag("custom")) {
            return custom;
        }
        return new NBTContainer();
    }

}
