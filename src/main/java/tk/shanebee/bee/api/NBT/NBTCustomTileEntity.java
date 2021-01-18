package tk.shanebee.bee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tk.shanebee.bee.api.NBTApi;

public class NBTCustomTileEntity extends NBTTileEntity implements NBTCustom {

    private final BlockState blockState;
    private final String KEY = "skbee-custom";

    /**
     * @param tile BlockState from any TileEntity
     */
    public NBTCustomTileEntity(BlockState tile) {
        super(tile);
        this.blockState = tile;
        if (NBTApi.HAS_PERSISTENCE) {
            convert();
        }
    }

    @Override
    public NBTCompound getCustomNBT() {
        return getPersistentDataContainer().getOrCreateCompound(KEY);
    }

    @Override
    public void deleteCustomNBT() {
        getPersistentDataContainer().removeKey(KEY);
    }

    private NBTCompound getCustomNBTCompound() {
        String bukkit = "PublicBukkitValues";
        NBTCompound compound = new NBTContainer(new NBTTileEntity(blockState).toString());
        NBTCompound custom = null;
        if (compound.hasKey(bukkit)) {
            NBTCompound persist = compound.getCompound(bukkit);
            persist.removeKey("__nbtapi"); // this is just a placeholder one, so we dont need it
            if (persist.hasKey(KEY)) {
                custom = getPersistentDataContainer().getCompound(KEY);
                persist.removeKey(KEY);
            }
            if (persist.getKeys().size() == 0) {
                compound.removeKey(bukkit);
            }
        }
        NBTCompound customCompound = compound.getOrCreateCompound("custom");
        if (custom != null) {
            customCompound.mergeCompound(custom);
        }
        return compound;
    }

    @Override
    public NBTCompound getOrCreateCompound(String name) {
        if (name.equals("custom")) {
            return getPersistentDataContainer().getOrCreateCompound(KEY);
        }
        return super.getOrCreateCompound(name);
    }

    @Override
    public Boolean hasKey(String key) {
        if (key.equalsIgnoreCase("custom")) {
            return true;
        }
        return super.hasKey(key);
    }

    @Override
    public String toString() {
        try {
            if (NBTApi.HAS_PERSISTENCE) {
                return getCustomNBTCompound().toString();
            }
            return super.toString();
        } catch (NbtApiException ignore) {
            return null;
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

}
