package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTCompound;
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
        NBTCompound data = super.getData();
        if (!data.hasTag("custom")) {
            setCustomCompound(data);
        } else if (!data.getString("id").equals(block.getType().getKey().toString())) {
            // If Block's type has changed, reset custom data
            data.clearNBT();
            setCustomCompound(data);
        }
        data.setInteger("x", block.getX());
        data.setInteger("y", block.getY());
        data.setInteger("z", block.getZ());
        return data;
    }

    private void setCustomCompound(NBTCompound data) {
        data.setString("id", block.getType().getKey().toString());
        data.getOrCreateCompound("custom");
    }

}
