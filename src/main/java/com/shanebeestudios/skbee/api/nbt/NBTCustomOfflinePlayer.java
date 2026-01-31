package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class NBTCustomOfflinePlayer extends NBTFile implements NBTCustom {

    private static final String PLAYER_FOLDER;

    static {
        String worldFolder = Bukkit.getWorlds().getFirst().getWorldFolder().getPath();
        PLAYER_FOLDER = worldFolder + "/playerdata/";
    }

    @SuppressWarnings("deprecation")
    public NBTCustomOfflinePlayer(OfflinePlayer offlinePlayer) throws IOException {
        super(new File(PLAYER_FOLDER + offlinePlayer.getUniqueId() + ".dat"));
    }

    @Override
    public NBTCompound getOrCreateCompound(String name) {
        if (name.equals("custom")) {
            return getCustomNBT();
        }
        return super.getOrCreateCompound(name);
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
            getOrCreateCompound("BukkitValues").removeKey(KEY);
        }
    }

    @Override
    @Contract("true -> !null")
    public NBTCompound getCustomNBT(boolean createTagIfMissing) {
        if (createTagIfMissing)
            return super.getOrCreateCompound("BukkitValues").getOrCreateCompound(KEY);
        NBTCompound bukkitValues = super.getCompound("BukkitValues");
        if (bukkitValues != null) {
            return bukkitValues.getCompound(KEY);
        }
        return null;
    }

    @Override
    public String toString() {
        return getCopy().toString();
    }

    @SuppressWarnings("deprecation")
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
                    custom = getCustomNBT();
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

}
