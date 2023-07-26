package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;

public class NBTCustomOfflinePlayer extends NBTFile implements NBTCustom {

    private static final String PLAYER_FOLDER;
    private final String KEY = "skbee-custom";

    static {
        String worldFolder = Bukkit.getWorlds().get(0).getWorldFolder().getPath();
        PLAYER_FOLDER = worldFolder + "/playerdata/";
    }

    public NBTCustomOfflinePlayer(OfflinePlayer offlinePlayer) throws IOException {
        super(new File(PLAYER_FOLDER + offlinePlayer.getUniqueId() + ".dat"));
    }

    @Override
    public NBTCompound getOrCreateCompound(String name) {
        if (name.equals("custom")) {
            return getPersistentDataContainer();
        }
        return super.getOrCreateCompound(name);
    }

    @Override
    public NBTCompound getCompound(String name) {
        if (name.equals("custom")) {
            return getPersistentDataContainer();
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
            getPersistentDataContainer().mergeCompound(custom);
        }
    }

    @Override
    public NBTType getType(String name) {
        if (name.equalsIgnoreCase("custom")) {
            return NBTType.NBTTagCompound;
        }
        return super.getType(name);
    }

    @Override
    protected void saveCompound() {
        try {
            super.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCustomNBT() {
        if (hasTag("BukkitValues")) {
            getCompound("BukkitValues").removeKey(KEY);
        }
    }

    @Override
    public String toString() {
        try {
            String bukkitValues = "BukkitValues";
            NBTCompound compound = new NBTContainer(super.toString());
            NBTCompound custom = null;
            if (compound.hasTag(bukkitValues)) {
                NBTCompound persist = compound.getCompound(bukkitValues);
                persist.removeKey("__nbtapi"); // this is just a placeholder one, so we don't need it
                if (persist.hasTag(KEY)) {
                    custom = getPersistentDataContainer();
                    persist.removeKey(KEY);
                }
                if (persist.getKeys().size() == 0) {
                    compound.removeKey(bukkitValues);
                }
            }
            NBTCompound customCompound = compound.getOrCreateCompound("custom");
            if (custom != null) {
                customCompound.mergeCompound(custom);
            }
            return compound.toString();
        } catch (NbtApiException ex) {
            if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private NBTCompound getPersistentDataContainer() {
        NBTCompound bukkitValues = super.getOrCreateCompound("BukkitValues");
        return bukkitValues.getOrCreateCompound(KEY);
    }

}
