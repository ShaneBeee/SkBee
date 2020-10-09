package tk.shanebee.bee.api.NBT;

import ch.njol.skript.Skript;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tk.shanebee.bee.SkBee;

public class NBTCustomTileEntity extends NBTTileEntity {

    private final TileState blockState;
    private NBTCompound customNBT;
    private final boolean HAS_PERSISTENCE = Skript.isRunningMinecraft(1, 14);
    private final NamespacedKey KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");

    /**
     * @param tile BlockState from any TileEntity
     */
    public NBTCustomTileEntity(TileState tile) {
        super(tile);
        this.blockState = tile;
        if (HAS_PERSISTENCE) {
            String data = null;
            PersistentDataContainer container = blockState.getPersistentDataContainer();
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
        PersistentDataContainer container = blockState.getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, customNBT.toString());
        blockState.update();
    }

    public void deleteCustomNBT() {
        PersistentDataContainer container = blockState.getPersistentDataContainer();
        if (container.has(KEY, PersistentDataType.STRING)) {
            container.remove(KEY);
        }
        blockState.update();
    }

    public NBTCompound getCustomNBTCompound() {
        if (!HAS_PERSISTENCE) {
            return this;
        }
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
        return getCustomNBTCompound().toString();
    }

}
