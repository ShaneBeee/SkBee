package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class NBTCustomBlock extends NBTBlock {

    private final Block block;

    public NBTCustomBlock(Block block) {
        super(block);
        this.block = block;
    }

    /**
     * Get the NBTCompound of this {@link Block}
     *
     * @return NBTCompound of this block
     */
    @Override
    public NBTCompound getData() {
        Chunk chunk = this.block.getChunk();
        NBTChunk nbtChunk = new NBTChunk(chunk);

        NBTCompound data = null;
        NBTCompound chunkData = nbtChunk.getPersistentDataContainer();
        String blockString = this.block.getX() + "_" + this.block.getY() + "_" + this.block.getZ();

        if (chunkData.hasTag("blocks", NBTType.NBTTagCompound) && chunkData.getCompound("blocks").hasTag(blockString)) {
            data = getSuperData();
            if (!data.getString("id").equals(block.getType().getKey().toString())) {
                // If Block's type has changed, remove custom NBT and return false container
                chunkData.getCompound("blocks").removeKey(blockString);
            }
        }
        if (data == null) return new NBTFalseContainer(this);
        return data;
    }

    private NBTCompound getSuperData() {
        return super.getData();
    }

    // Creates a false NBT container that isn't saved to the chunk
    // This is useful in cases where you're just checking for NBT
    // but not actually saving anything, therefor no need to clog up chunks
    private static class NBTFalseContainer extends NBTContainer {

        NBTCustomBlock customBlock;
        private final boolean canSave;

        public NBTFalseContainer(NBTCustomBlock customBlock) {
            super();
            this.customBlock = customBlock;
            setInteger("x", this.customBlock.block.getX());
            setInteger("y", this.customBlock.block.getY());
            setInteger("z", this.customBlock.block.getZ());
            setString("id", this.customBlock.block.getType().getKey().toString());
            this.canSave = true;
        }

        @Override
        protected void saveCompound() {
            super.saveCompound();
            if (!this.canSave) return;
            // Only save to super data if custom values have been added
            // At this point the x/y/z/id will also be saved
            this.customBlock.getSuperData().mergeCompound(this);
        }
    }

}
