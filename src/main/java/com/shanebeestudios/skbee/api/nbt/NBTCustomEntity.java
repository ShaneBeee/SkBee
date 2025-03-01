package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class NBTCustomEntity extends NBTEntity implements NBTCustom {

    private final Entity entity;

    /**
     * @param entity Any valid Bukkit Entity
     */
    public NBTCustomEntity(Entity entity) {
        super(entity);
        this.entity = entity;
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
            NBTCompound custom = comp.getCompound("custom");
            getPersistentDataContainer().getOrCreateCompound(KEY).mergeCompound(custom);
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
    public @NotNull NBTCompound getCopy() {
        try {
            String bukkit = "BukkitValues";
            NBTCompound compound = new NBTContainer();
            compound.mergeCompound(this); // create a copy
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
            return compound;
        } catch (NbtApiException ex) {
            if (SkBee.isDebug()) {
                ex.printStackTrace();
            }
            return new NBTContainer();
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
