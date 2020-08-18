package tk.shanebee.bee.api;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.reflection.SkReflection;
import tk.shanebee.bee.api.util.Util;
import tk.shanebee.bee.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Main NBT api for SkBee
 */
public class NBTApi {

    private final Config CONFIG;

    public NBTApi() {
        CONFIG = SkBee.getPlugin().getPluginConfig();
    }

    /** Validate an NBT string
     * @param nbtString NBT string to validate
     * @return True if NBT string is valid, otherwise false
     */
    public boolean validateNBT(String nbtString) {
        if (nbtString == null) return false;
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


    /**
     * Force the NBT-API to load
     * <p>This is used to force load the API and make sure its compatible
     * before actually loading NBT elements. We want to make sure its compatible
     * before the user actually executes their code.</p>
     */
    public void forceLoadNBT() {
        Util.log("&aLoading NBTApi...");
        NBTItem loadingItem = new NBTItem(new ItemStack(Material.STONE));
        loadingItem.mergeCompound(new NBTContainer("{}"));
        Util.log("&aSuccessfully loaded NBTApi!");
    }

    private File getFile(String fileName) {
        fileName = !fileName.endsWith(".dat") ? fileName + ".dat" : fileName;
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        } else {
            return file;
        }
    }

    /**
     * Add NBT to an object.
     *
     * @param object Object to add NBT to
     * @param value NBT string to add to object
     * @param type Type of object
     * @return Object with the new NBT value
     */
    @Nullable
    public Object addNBT(@NotNull Object object, @NotNull String value, @NotNull ObjectType type) {
        if (!type.isAssignableFrom(object, CONFIG.SETTINGS_DEBUG))
            return null;
        if (!validateNBT(value)) return null;
        switch (type) {
            case FILE:
                File file = getFile(((String) object));
                if (file == null) return null;

                try {
                    NBTFile nbtFile = new NBTFile(file);
                    nbtFile.mergeCompound(new NBTContainer(value));
                    nbtFile.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case ITEM_STACK:
                NBTItem item = new NBTItem(((ItemStack) object));
                item.mergeCompound(new NBTContainer(value));
                return item.getItem();
            case ITEM_TYPE:
                ItemStack stack = ((ItemType) object).getRandom();
                if (stack == null) return null;

                NBTItem nbtItemType = new NBTItem(stack);
                nbtItemType.mergeCompound(new NBTContainer(value));
                SkReflection.setMeta((ItemType) object, nbtItemType.getItem().getItemMeta());
                return object;
            case SLOT:
                ItemStack slotItemStack = ((Slot) object).getItem();
                if (slotItemStack != null) {
                    ((Slot) object).setItem((ItemStack) addNBT(slotItemStack, value, ObjectType.ITEM_STACK));
                }
                return object;
            case ENTITY:
                NBTEntity nbtEntity = new NBTEntity(((Entity) object));
                nbtEntity.mergeCompound(new NBTContainer(value));
                return object;
            case BLOCK:
                NBTTileEntity tile = new NBTTileEntity(((Block) object).getState());
                try {
                    tile.mergeCompound(new NBTContainer(value));
                } catch (NbtApiException ignore) {}
                return object;
            default:
                if (CONFIG.SETTINGS_DEBUG)
                    throw new IllegalArgumentException("Unsupported ObjectType: " + type);
        }
        return null;
    }

    /**
     * Set NBT for an object.
     *
     * @param object Object to set NBT for
     * @param value NBT string to set to object
     * @param type Type of object
     * @return Object with the new NBT value
     */
    @Nullable
    public Object setNBT(@NotNull Object object, @NotNull String value, @NotNull ObjectType type) {
        if (!type.isAssignableFrom(object, CONFIG.SETTINGS_DEBUG))
            return null;
        if (!validateNBT(value)) return null;
        switch (type) {
            case FILE:
                return addNBT(object, value, ObjectType.FILE);
            case ITEM_STACK:
                ItemStack stack = new ItemStack(((ItemStack) object).getType());
                NBTItem nbtItemStack = new NBTItem(stack);
                nbtItemStack.mergeCompound(new NBTContainer(value));
                ((ItemStack) object).setItemMeta(nbtItemStack.getItem().getItemMeta());
                return object;
            case ITEM_TYPE:
                ItemStack itemStack = new ItemStack(((ItemType) object).getMaterial());
                NBTItem nbtItemType = new NBTItem(itemStack);
                nbtItemType.mergeCompound(new NBTContainer(value));
                SkReflection.setMeta((ItemType) object, nbtItemType.getItem().getItemMeta());
                return object;
            case SLOT:
                ItemStack slotItemStack = ((Slot) object).getItem();
                if (slotItemStack != null) {
                    ((Slot) object).setItem((ItemStack) setNBT(slotItemStack, value, ObjectType.ITEM_STACK));
                }
                return object;
            case ENTITY:
                return addNBT(object, value, ObjectType.ENTITY);
            case BLOCK:
                return addNBT(object, value, ObjectType.BLOCK);
            default:
                if (CONFIG.SETTINGS_DEBUG)
                    throw new IllegalArgumentException("Unsupported ObjectType: " + type);
        }
        return null;
    }

    /**
     * Get NBT from an object
     *
     * @param object Object to get NBT from
     * @param type Type of object
     * @return NBT string of object
     */
    @Nullable
    public String getNBT(@NotNull Object object, @NotNull ObjectType type) {
        if (!type.isAssignableFrom(object, CONFIG.SETTINGS_DEBUG))
            return null;
        switch (type) {
            case FILE:
                File file = getFile(((String) object));
                if (file == null) return null;
                NBTFile fileNBT = null;
                try {
                    fileNBT = new NBTFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fileNBT == null) return null;
                return fileNBT.toString();
            case ITEM_STACK:
                ItemStack itemStack = (ItemStack) object;
                if (itemStack.getType() == Material.AIR) return null;
                NBTItem item = new NBTItem(itemStack);
                return item.toString();
            case ITEM_STACK_FULL:
                return NBTItem.convertItemtoNBT((ItemStack) object).toString();
            case ITEM_TYPE:
                ItemStack itemTypeStack = ((ItemType) object).getRandom();
                if (itemTypeStack == null) return null;
                return getNBT(itemTypeStack, ObjectType.ITEM_STACK);
            case ITEM_TYPE_FULL:
                ItemStack itemTypeStackFull = ((ItemType) object).getRandom();
                if (itemTypeStackFull == null) return null;
                return getNBT(itemTypeStackFull, ObjectType.ITEM_STACK_FULL);
            case SLOT:
                ItemStack slotItemStack = ((Slot) object).getItem();
                if (slotItemStack == null) return null;
                return getNBT(slotItemStack, ObjectType.ITEM_STACK);
            case SLOT_FULL:
                ItemStack slotItemStackFull = ((Slot) object).getItem();
                if (slotItemStackFull == null) return null;
                return getNBT(slotItemStackFull, ObjectType.ITEM_STACK_FULL);
            case ENTITY:
                Entity entity = (Entity) object;
                if (entity.isDead()) return null;
                NBTEntity nbtEntity = new NBTEntity(entity);
                return nbtEntity.toString();
            case BLOCK:
                NBTTileEntity tile = new NBTTileEntity(((Block) object).getState());
                try {
                    return tile.getCompound().toString();
                } catch (NbtApiException ignore) {
                    return null;
                }
            default:
                if (CONFIG.SETTINGS_DEBUG)
                    throw new IllegalArgumentException("Unsupported ObjectType: " + type);
        }
        return null;
    }

    /**
     * Get an {@link ItemType} from an NBT string
     *
     * @param nbt Full NBT string
     * @return New ItemType from NBT string
     */
    public ItemType getItemTypeFromNBT(String nbt) {
        if (!validateNBT(nbt)) return null;
        return new ItemType(getItemStackFromNBT(nbt));
    }

    /**
     * Get an {@link ItemStack} from an NBT string
     *
     * @param nbt Full NBT string
     * @return New ItemStack from NBT string
     */
    public ItemStack getItemStackFromNBT(String nbt) {
        if (!validateNBT(nbt)) return null;
        NBTContainer container = new NBTContainer(nbt);
        return NBTItem.convertNBTtoItem(container);
    }

    /**
     * Get a specific tag from an NBT string
     *
     * @param tag Tag to check for
     * @param nbt NBT to grab tag from
     * @return Object from the NBT string
     */
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
                List<Object> list = new ArrayList<>();
                for (NBTCompound comp : compound.getCompoundList(tag)) {
                    list.add(comp.toString());
                }
                list.addAll(compound.getDoubleList(tag));
                list.addAll(compound.getFloatList(tag));
                list.addAll(compound.getIntegerList(tag));
                list.addAll(compound.getStringList(tag));
                list.addAll(compound.getLongList(tag));
                return list;
            default:
                if (CONFIG.SETTINGS_DEBUG)
                    throw new IllegalArgumentException("Unknown tag type, please let the dev know -> type: " + type.toString());
        }
        return null;
    }

    /**
     * Type of object used for getting/setting/adding NBT
     */
    public enum ObjectType {
        /**
         * Represents an {@link ItemType} object
         */
        ITEM_TYPE(ItemType.class),
        /**
         * Represents an {@link ItemType} object
         * <p>This is used when getting full NBT of an ItemType</p>
         */
        ITEM_TYPE_FULL(ItemType.class),
        /**
         * Represents an {@link ItemStack} object
         */
        ITEM_STACK(ItemStack.class),
        /**
         * Represents an {@link ItemStack} object
         * <p>This is used when getting full NBT of an ItemStack</p>
         */
        ITEM_STACK_FULL(ItemStack.class),
        /**
         * Represents a {@link Slot} object
         */
        SLOT(Slot.class),
        /**
         * Represents a {@link Slot} object
         * <p>This is used when getting full NBT of a Slot item</p>
         */
        SLOT_FULL(Slot.class),
        /**
         * Represents an {@link Entity} object
         */
        ENTITY(Entity.class),
        /**
         * Represents a {@link Block} object
         */
        BLOCK(Block.class),
        /**
         * Represents a {@link String} object (used for files)
         */
        FILE(String.class);

        Class<?> cl;

        ObjectType(Class<?> classType) {
            this.cl = classType;
        }

        /**
         * Check if the object is assignable from the class type
         *
         * @param object Object to compare
         * @return True if object matches the class type
         */
        public boolean isAssignableFrom(Object object, boolean debug) {
            if (cl.isAssignableFrom(object.getClass()))
                return true;
            if (debug)
                throw new IllegalArgumentException("Object is not assignable from ObjectType:\n\tObject: " + object + " = " + object.getClass() +
                        "\n\tObjectType: " + this + "\n\tAssignableFrom: " + cl);
            return false;
        }
    }

}
