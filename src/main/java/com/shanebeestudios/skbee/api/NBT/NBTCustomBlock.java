package com.shanebeestudios.skbee.api.NBT;

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
            return setData(data);
        } else if (!data.getString("id").equals(block.getType().getKey().toString())) {
            // If Block's type has changed, reset custom data
            if (data.hasTag("custom")) {
                data.removeKey("custom");
            }
            return setData(data);
        } else if (data.getInteger("x") != block.getX()) {
            return setData(data);
        } else if (data.getInteger("y") != block.getY()) {
            return setData(data);
        } else if (data.getInteger("z") != block.getZ()) {
            return setData(data);
        }
        return data;
    }

    private NBTCompound setData(NBTCompound data) {
        data.setString("id", block.getType().getKey().toString());
        data.setInteger("x", block.getX());
        data.setInteger("y", block.getY());
        data.setInteger("z", block.getZ());
        data.getOrCreateCompound("custom");
        return data;
    }

}
