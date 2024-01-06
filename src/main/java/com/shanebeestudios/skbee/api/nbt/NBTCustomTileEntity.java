package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NBTCustomTileEntity extends NBTTileEntity implements NBTCustom {

    private final BlockState blockState;

    /**
     * @param tile BlockState from any TileEntity
     */
    public NBTCustomTileEntity(BlockState tile) {
        super(tile);
        this.blockState = tile;
        convert();
    }

    @Override
    public void deleteCustomNBT() {
        getPersistentDataContainer().removeKey(KEY);
    }

    @Override
    public NBTCompound getOrCreateCompound(String name) {
        if (name.equals("custom")) {
            return getPersistentDataContainer().getOrCreateCompound(KEY);
        }
        try {
            return super.getOrCreateCompound(name);
        } catch (NbtApiException ignore) {
            return null;
        }
    }

    @Override
    public NBTCompound getCompound(String name) {
        if (name.equals("custom")) {
            return getPersistentDataContainer().getOrCreateCompound(KEY);
        }
        return super.getCompound(name);
    }

    @Override
    public boolean hasTag(String key) {
        if (key.equalsIgnoreCase("custom")) {
            return true;
        }
        return super.hasTag(key);
    }

    @Override
    public void mergeCompound(NBTCompound comp) {
        super.mergeCompound(comp);
        if (comp.hasTag("custom")) {
            NBTCompound custom = comp.getOrCreateCompound("custom");
            NBTCompound customNBT = getPersistentDataContainer().getOrCreateCompound(KEY);
            customNBT.mergeCompound(custom);
        }
    }

    @Override
    public NBTType getType(String name) {
        if (name.equalsIgnoreCase("custom")) {
            return NBTType.NBTTagCompound;
        }
        return super.getType(name);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String toString() {
        return getCopy().toString();
    }

    @Override
    public NBTCompound getCopy() {
        try {
            String bukkit = "PublicBukkitValues";
            NBTCompound compound = new NBTContainer(new NBTTileEntity(blockState).toString());
            NBTCompound custom = null;
            if (compound.hasTag(bukkit)) {
                NBTCompound persist = compound.getCompound(bukkit);
                assert persist != null;
                persist.removeKey("__nbtapi"); // this is just a placeholder one, so we don't need it
                if (persist.hasTag(KEY)) {
                    custom = getPersistentDataContainer().getCompound(KEY);
                    persist.removeKey(KEY);
                }
                if (persist.getKeys().isEmpty()) {
                    compound.removeKey(bukkit);
                }
            }
            NBTCompound customCompound = compound.getOrCreateCompound("custom");
            if (custom != null) {
                customCompound.mergeCompound(custom);
            }
            // For some reason block NBT doesn't show location in NBT-API (it does in vanilla MC)
            compound.setInteger("x", blockState.getX());
            compound.setInteger("y", blockState.getY());
            compound.setInteger("z", blockState.getZ());
            return compound;
        } catch (NbtApiException ignore) {
            return new NBTContainer();
        }
    }

    private void convert() {
        PersistentDataContainer container = ((TileState) blockState).getPersistentDataContainer();
        if (container.has(OLD_KEY, PersistentDataType.STRING)) {
            String data = container.get(OLD_KEY, PersistentDataType.STRING);
            container.remove(OLD_KEY);
            if (data != null) {
                blockState.update();
                NBTCompound custom = getOrCreateCompound("custom");
                custom.mergeCompound(new NBTContainer(data));
            }
        }
    }

    @Override
    protected void saveCompound() {
        super.saveCompound();
        blockState.getBlock().getState().update();
    }
}
