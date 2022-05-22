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
            return setData(data);
        } else if (!data.getString("id").equals(block.getType().getKey().toString())) {
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
        data.getKeys().forEach(data::removeKey);
        data.setString("id", block.getType().getKey().toString());
        data.setInteger("x", block.getX());
        data.setInteger("y", block.getY());
        data.setInteger("z", block.getZ());
        data.getOrCreateCompound("custom");
        return data;
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
