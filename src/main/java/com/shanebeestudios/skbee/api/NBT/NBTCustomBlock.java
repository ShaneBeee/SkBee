package com.shanebeestudios.skbee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
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
        if (!data.hasKey("custom")) {
            setData(data);
        } else if (!data.getString("id").equals(block.getType().getKey().toString())) {
            setData(data);
        } else if (data.getInteger("x") != block.getX()) {
            setData(data);
        } else if (data.getInteger("y") != block.getY()) {
            setData(data);
        } else if (data.getInteger("z") != block.getZ()) {
            setData(data);
        }
        return data;
    }

    private void setData(NBTCompound data) {
        data.getKeys().forEach(data::removeKey);
        data.setString("id", block.getType().getKey().toString());
        data.setInteger("x", block.getX());
        data.setInteger("y", block.getY());
        data.setInteger("z", block.getZ());
        data.getOrCreateCompound("custom");
    }

    /**
     * Get the 'custom' tag of this {@link Block Block's} NBTCompound
     *
     * @return 'custom' tag of this Block's NBTCompound
     */
    public NBTCompound getCustomData() {
        return getData().getOrCreateCompound("custom");
    }

    public NBTCompound cloneCustomData() {
        return new NBTContainer(getCustomData().toString());
    }

}
