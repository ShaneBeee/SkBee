package tk.shanebee.bee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;

public class NBTCustomTileEntity extends NBTTileEntity {

    private final BlockState blockState;
    private NBTCompound customNBT;
    private NamespacedKey KEY;

    /**
     * @param tile BlockState from any TileEntity
     */
    public NBTCustomTileEntity(BlockState tile) {
        super(tile);
        this.blockState = tile;
        if (NBTApi.HAS_PERSISTENCE) {
            KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");
            String data = null;
            PersistentDataContainer container = ((TileState) blockState).getPersistentDataContainer();
            if (container.has(KEY, PersistentDataType.STRING)) {
                data = container.get(KEY, PersistentDataType.STRING);
            }
            customNBT = new NBTContainer(data != null ? data : "{}");
        }
        blockState.update();
    }

    public NBTCompound getCustomNBT() {
        return customNBT;
    }

    public void setCustomNBT(NBTCompound customNBT) {
        this.customNBT = customNBT;
        PersistentDataContainer container = ((TileState) blockState).getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, customNBT.toString());
        blockState.update();
    }

    public void deleteCustomNBT() {
        PersistentDataContainer container = ((TileState) blockState).getPersistentDataContainer();
        if (container.has(KEY, PersistentDataType.STRING)) {
            container.remove(KEY);
        }
        blockState.update();
    }

    public NBTCompound getCustomNBTCompound() {
        NBTCompound compound = new NBTContainer(this.getCompound().toString());
        if (compound.hasKey("PublicBukkitValues")) {
            NBTCompound persist = compound.getCompound("PublicBukkitValues");
            persist.removeKey("skbee:custom-nbt");
            if (persist.getKeys().size() == 0) {
                compound.removeKey("PublicBukkitValues");
            }
        }
        NBTCompound customCompound = compound.addCompound("custom");
        if (customNBT != null) {
            customCompound.mergeCompound(customNBT);
        }
        return compound;
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

}
