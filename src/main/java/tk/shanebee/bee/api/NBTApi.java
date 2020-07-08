package tk.shanebee.bee.api;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.reflection.SkReclection;
import tk.shanebee.bee.api.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NBTApi {

    @SuppressWarnings("ConstantConditions")
    public boolean validateNBT(Expression<String> nbt) {
        if (nbt == null) {
            sendError("null", null);
            return false;
        }
        for (String nbtString : nbt.getAll(null)) {
            try {
                new NBTContainer(nbtString);
            } catch (Exception ex) {
                sendError(nbtString, ex);
                return false;
            }
        }
        return true;
    }

    public boolean validateNBT(String nbtString) {
        try {
            new NBTContainer(nbtString);
        } catch (Exception ex) {
            sendError(nbtString, ex);
            return false;
        }
        return true;
    }

    private void sendError(String error, Exception exception) {
        Util.skriptError("&cInvalid NBT: &b" + error + "&c");
        if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG && exception != null) {
            exception.printStackTrace();
        }
    }

    // This is just to force load the api!
    public void forceLoadNBT() {
        SkBee.log("&aLoading NBTApi!");
        NBTItem loadingItem = new NBTItem(new ItemStack(Material.STONE));
        loadingItem.mergeCompound(new NBTContainer("{}"));
        SkBee.log("&aNBTApi successfully loaded!");
    }

    // ITEM NBT
    public void setNBT(ItemType itemType, String value) {
        if (!validateNBT(value)) return;
        ItemStack itemStack = new ItemStack(itemType.getMaterial());

        NBTItem item = new NBTItem(itemStack);
        item.mergeCompound(new NBTContainer(value));
        SkReclection.setMeta(itemType, item.getItem().getItemMeta());
    }

    public void setNBT(ItemStack itemStack, String value) {
        if (!validateNBT(value)) return;
        ItemStack stack = new ItemStack(itemStack.getType());
        NBTItem item = new NBTItem(stack);
        item.mergeCompound(new NBTContainer(value));
        itemStack.setItemMeta(item.getItem().getItemMeta());
    }

    public void addNBT(ItemType itemType, String value) {
        if (!validateNBT(value)) return;
        ItemStack stack = itemType.getRandom();
        if (stack == null) return;

        NBTItem item = new NBTItem(stack);
        item.mergeCompound(new NBTContainer(value));
        SkReclection.setMeta(itemType, item.getItem().getItemMeta());
    }

    public void addNBT(ItemStack itemStack, String value) {
        if (!validateNBT(value)) return;
        NBTItem item = new NBTItem(itemStack);
        item.mergeCompound(new NBTContainer(value));
        itemStack.setItemMeta(item.getItem().getItemMeta());
    }

    public String getNBT(ItemType itemType) {
        ItemStack itemStack = itemType.getRandom();
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;

        NBTItem item = new NBTItem(itemType.getRandom());
        return item.toString();
    }

    public String getNBT(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        NBTItem item = new NBTItem(itemStack);
        return item.toString();
    }

    // ENTITY NBT
    public void setNBT(Entity entity, String newValue) {
        if (entity == null || entity.isDead()) return;
        if (!validateNBT(newValue)) return;
        addNBT(entity, newValue);
    }

    public void addNBT(Entity entity, String newValue) {
        if (entity == null || entity.isDead()) return;
        if (!validateNBT(newValue)) return;
        NBTEntity nbtEntity = new NBTEntity(entity);
        nbtEntity.mergeCompound(new NBTContainer(newValue));
    }

    public String getNBT(Entity entity) {
        if (entity == null || entity.isDead()) return null;
        NBTEntity nbtEntity = new NBTEntity(entity);
        return nbtEntity.toString();
    }

    // TILE ENTITY NBT
    public void setNBT(Block block, String newValue) {
        if (!validateNBT(newValue)) return;
        addNBT(block, newValue);
    }

    public void addNBT(Block block, String newValue) {
        if (!validateNBT(newValue)) return;
        NBTTileEntity tile = new NBTTileEntity(block.getState());
        tile.mergeCompound(new NBTContainer(newValue));
    }

    public String getNBT(Block block) {
        NBTTileEntity tile = new NBTTileEntity(block.getState());
        return tile.toString();
    }

    // FILE NBT
    public String getNBT(String fileName) {
        File file = getFile(fileName);
        if (file == null) return null;
        NBTFile fileNBT = null;
        try {
            fileNBT = new NBTFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileNBT == null) return null;
        return fileNBT.toString();
    }

    public void addNBT(String file, String value) {
        if (!validateNBT(value)) return;
        File file1 = getFile(file);
        if (file1 == null) return;

        try {
            NBTFile nbtFile = new NBTFile(file1);
            nbtFile.mergeCompound(new NBTContainer(value));
            nbtFile.save();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNBT(String file, String value) {
        if (!validateNBT(value)) return;
        // TODO fix this up
        addNBT(file, value);
    }

    // Internal use to get file NBT
    private File getFile(String fileName) {
        fileName = !fileName.endsWith(".dat") ? fileName + ".dat" : fileName;
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        } else {
            return file;
        }
    }

    // TAGS
    public Object getTag(String tag, String nbt) {
        if (nbt == null) return null;
        NBTCompound compound = new NBTContainer(nbt);
        NBTType type = compound.getType(tag);
        switch (type) {
            case NBTTagString:
                return compound.getString(tag);
            case NBTTagInt:
                return compound.getInteger(tag);
            case NBTTagIntArray:
                UUID uuid = compound.getUUID(tag);
                if (uuid != null) {
                    return uuid;
                } else {
                    return Collections.singletonList(compound.getIntArray(tag));
                }
            case NBTTagFloat:
                return compound.getFloat(tag);
            case NBTTagShort:
                return compound.getShort(tag);
            case NBTTagDouble:
                return compound.getDouble(tag);
            case NBTTagEnd:
                //return compound.toString(); // let's leave this here just in case
                return null;
            case NBTTagLong:
                return compound.getLong(tag);
            case NBTTagByte:
                return compound.getByte(tag);
            case NBTTagByteArray:
                return compound.getByteArray(tag);
            case NBTTagCompound:
                return compound.getCompound(tag).toString();
            case NBTTagList:
                List<String> stringList = new ArrayList<>();
                for (NBTCompound comp : compound.getCompoundList(tag)) {
                    stringList.add(comp.toString());
                }
                return stringList;
            default:
                return "null -> type: " + type.toString();
        }
    }

}
