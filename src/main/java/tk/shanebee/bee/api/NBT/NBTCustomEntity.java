package tk.shanebee.bee.api.NBT;

import ch.njol.skript.Skript;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import tk.shanebee.bee.SkBee;

public class NBTCustomEntity extends NBTEntity {

    private final Entity entity;
    private NBTCompound customNBT;
    private final boolean HAS_PERSISTENCE = Skript.isRunningMinecraft(1, 14);
    private final NamespacedKey KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");

    /**
     * @param entity Any valid Bukkit Entity
     */
    public NBTCustomEntity(Entity entity) {
        super(entity);
        this.entity = entity;
        if (HAS_PERSISTENCE) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            if (container.has(KEY, PersistentDataType.STRING)) {
                String data = container.get(KEY, PersistentDataType.STRING);

                customNBT = new NBTContainer(data != null ? data : "{}");
            }
        }
    }

    public NBTCompound getCustomNBT() {
        return customNBT;
    }

    public void setCustomNBT(NBTCompound customNBT) {
        this.customNBT = customNBT;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(KEY, PersistentDataType.STRING, customNBT.toString());
    }

    public void deleteCustomNBT() {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(KEY, PersistentDataType.STRING)) {
            container.remove(KEY);
        }
    }

    public NBTCompound getCustomNBTCompound() {
        if (!HAS_PERSISTENCE) {
            return this;
        }
        NBTCompound compound = new NBTContainer(this.getCompound().toString());
        if (compound.hasKey("BukkitValues")) {
            NBTCompound persist = compound.getCompound("BukkitValues");
            persist.removeKey("skbee:custom-nbt");
            if (persist.getKeys().size() == 0) {
                compound.removeKey("BukkitValues");
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
