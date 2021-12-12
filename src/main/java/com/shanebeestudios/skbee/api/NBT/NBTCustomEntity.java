package com.shanebeestudios.skbee.api.NBT;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NBTCustomEntity extends NBTEntity implements NBTCustom {

    private final Entity entity;
    private final String KEY = "skbee-custom";

    /**
     * @param entity Any valid Bukkit Entity
     */
    public NBTCustomEntity(Entity entity) {
        super(entity);
        this.entity = entity;
        convert();
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
        String bukkit = "BukkitValues";
        NBTCompound compound = new NBTContainer(new NBTEntity(entity).toString());
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
        try {
            return super.getOrCreateCompound(name);
        } catch (NbtApiException ignore) {
            return null;
        }
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
            return getCustomNBTCompound().toString();
        } catch (NbtApiException ignore) {
            return null;
        }
    }

    private void convert() {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(OLD_KEY, PersistentDataType.STRING)) {
            String data = container.get(OLD_KEY, PersistentDataType.STRING);
            NBTCompound custom = getOrCreateCompound("custom");
            if (data != null) {
                custom.mergeCompound(new NBTContainer(data));
            }
            container.remove(OLD_KEY);
        }
    }

}
