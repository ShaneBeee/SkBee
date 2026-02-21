package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
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
    @Contract("true -> !null")
    public NBTCompound getCustomNBT(boolean createTagIfMissing) {
        if (createTagIfMissing)
            return getPersistentDataContainer().getOrCreateCompound(KEY);
        return getPersistentDataContainer().getCompound(KEY);
    }

    @Override
    public NBTCompound getOrCreateCompound(String name) {
        if (name.equals("data")) {
            // NBT-API doesn't properly support the "data" compound in NBTEntity
            // This is probably due to internally Minecraft doesn't straight up use a compound
            // It uses a CustomData class which houses the compound inside
            // We can return a temp container and apply it back to the entity when saving
            NBTContainer tempContainer = new NBTContainer(this.getCompound()) {
                @Override
                protected void saveCompound() {
                    super.saveCompound();
                    NBTCompound data = getCompound("data");
                    if (data != null) {
                        NBT.modify(NBTCustomEntity.this.entity, readWriteNBT -> {
                            readWriteNBT.getOrCreateCompound("data").mergeCompound(data);
                        });
                    }
                }
            };
            return tempContainer.getOrCreateCompound(name);
        }
        if (name.equals("custom")) {
            return getCustomNBT();
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
            return getCustomNBT();
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
            getCustomNBT().mergeCompound(custom);
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
