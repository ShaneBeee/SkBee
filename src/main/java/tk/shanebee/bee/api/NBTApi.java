package tk.shanebee.bee.api;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBT.NBTCustomEntity;
import tk.shanebee.bee.api.NBT.NBTCustomTileEntity;
import tk.shanebee.bee.api.reflection.SkReflection;
import tk.shanebee.bee.api.util.MathUtil;
import tk.shanebee.bee.api.util.Util;
import tk.shanebee.bee.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main NBT api for SkBee
 */
public class NBTApi {

    public static final boolean HAS_PERSISTENCE = Skript.isRunningMinecraft(1, 14);
    @SuppressWarnings("ConstantConditions")
    public static final boolean SUPPORTS_BLOCK_NBT = Skript.isRunningMinecraft(1, 16, 4) &&
            PersistentDataHolder.class.isAssignableFrom(Chunk.class);
    private final Config CONFIG;

    public NBTApi() {
        CONFIG = SkBee.getPlugin().getPluginConfig();
    }

    /**
     * Validate an NBT string
     *
     * @param nbtString NBT string to validate
     * @return True if NBT string is valid, otherwise false
     */
    public static boolean validateNBT(String nbtString) {
        if (nbtString == null) return false;
        try {
            new NBTContainer(nbtString);
        } catch (Exception ex) {
            sendError(nbtString, ex);
            return false;
        }
        return true;
    }

    private static void sendError(String error, Exception exception) {
        Util.skriptError("&cInvalid NBT: &b" + error + "&c");
        if (exception == null) return;

        if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
            exception.printStackTrace();
        } else {
            String cause = exception.getCause().getCause().getCause().toString();
            if (cause.contains("CommandSyntaxException")) {
                String[] split = cause.split("CommandSyntaxException: ");
                if (split.length > 1) {
                    Util.skriptError("&cCause: &e" + split[1]);
                }
            }

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
        fileName = !fileName.endsWith(".dat") && !fileName.endsWith(".nbt") ? fileName + ".dat" : fileName;
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
     * @param value  NBT string to add to object
     * @param type   Type of object
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
                ItemStack itemStack = (ItemStack) object;
                if (itemStack.getType() == Material.AIR) return itemStack;
                NBTItem item = new NBTItem(itemStack);
                item.mergeCompound(new NBTContainer(value));
                ItemMeta meta = item.getItem().getItemMeta();
                if (meta == null) return itemStack;
                itemStack.setItemMeta(item.getItem().getItemMeta());
                return item.getItem();
            case ITEM_TYPE:
                ItemStack stack = ((ItemType) object).getItem().getRandom();
                if (stack == null || stack.getType() == Material.AIR) return object;

                NBTItem nbtItemType = new NBTItem(stack);
                nbtItemType.mergeCompound(new NBTContainer(value));
                ItemMeta itemMeta = nbtItemType.getItem().getItemMeta();
                if (itemMeta == null) return object;
                SkReflection.setMeta((ItemType) object, nbtItemType.getItem().getItemMeta());
                return object;
            case SLOT:
                ItemStack slotItemStack = ((Slot) object).getItem();
                if (slotItemStack != null && slotItemStack.getType() != Material.AIR) {
                    ((Slot) object).setItem((ItemStack) addNBT(slotItemStack, value, ObjectType.ITEM_STACK));
                }
                return object;
            case ENTITY:
                NBTCustomEntity nbtEntity = new NBTCustomEntity((Entity) object);
                NBTCompound nbtCompound = new NBTContainer(value);
                if (nbtCompound.hasKey("custom")) {
                    NBTCompound custom = nbtEntity.getCustomNBT();
                    custom.mergeCompound(nbtCompound.getCompound("custom"));
                    nbtEntity.setCustomNBT(custom);
                }
                nbtEntity.mergeCompound(nbtCompound);
                return object;
            case BLOCK:
                BlockState blockState = ((Block) object).getState();
                //if (HAS_PERSISTENCE && !(blockState instanceof TileState)) return object;

                if (isTileEntity(blockState)) {
                    NBTCustomTileEntity nbtBlock = new NBTCustomTileEntity((blockState));
                    NBTCompound updated = new NBTContainer(value);
                    if (updated.hasKey("custom")) {
                        NBTCompound custom = nbtBlock.getCustomNBT();
                        custom.mergeCompound(updated.getCompound("custom"));
                        nbtBlock.setCustomNBT(custom);
                    }
                    nbtBlock.mergeCompound(updated);
                }
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
     * @param value  NBT string to set to object
     * @param type   Type of object
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
     * @param type   Type of object
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
                ItemStack itemTypeStack = ((ItemType) object).getItem().getRandom();
                if (itemTypeStack == null) return null;
                return getNBT(itemTypeStack, ObjectType.ITEM_STACK);
            case ITEM_TYPE_FULL:
                ItemStack itemTypeStackFull = ((ItemType) object).getItem().getRandom();
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
     * Get an {@link ItemType} from an {@link NBTCompound}
     *
     * @param nbt Full NBT Compound
     * @return New ItemType from NBT Compound
     */
    public ItemType getItemTypeFromNBT(NBTCompound nbt) {
        return new ItemType(getItemStackFromNBT(nbt));
    }

    /**
     * Get an {@link ItemStack} from an {@link NBTCompound}
     *
     * @param nbt Full NBT Compound
     * @return New ItemStack from NBT Compound
     */
    public ItemStack getItemStackFromNBT(NBTCompound nbt) {
        return NBTItem.convertNBTtoItem(nbt);
    }

    /**
     * Delete a tag from an {@link NBTCompound}
     *
     * @param tag         Tag to delete
     * @param nbtCompound Compound to remove tag from
     */
    public void deleteTag(@NotNull String tag, @NotNull NBTCompound nbtCompound) {
        NBTCompound compound = nbtCompound;
        String key = tag;
        if (tag.contains(";")) {
            String[] splits = tag.split(";");
            for (int i = 0; i < splits.length - 1; i++) {
                if (compound.hasKey(splits[i])) {
                    compound = compound.getCompound(splits[i]);
                }
            }
            key = splits[splits.length - 1];
        }
        compound.removeKey(key);
    }

    /**
     * Set a specific tag of an {@link NBTCompound}
     *
     * @param tag         Tag that will be set
     * @param nbtCompound Compound to change
     * @param object      Value of tag to set to
     */
    public void setTag(@NotNull String tag, @NotNull NBTCompound nbtCompound, @NotNull Object[] object) {
        NBTCompound compound = nbtCompound;
        String key = tag;
        if (tag.contains(";")) {
            String[] splits = tag.split(";");
            for (int i = 0; i < splits.length - 1; i++) {
                if (compound.hasKey(splits[i])) {
                    compound = compound.getCompound(splits[i]);
                }
            }
            key = splits[splits.length - 1];
        }

        boolean custom = !compound.hasKey(key);
        boolean isSingle = object.length == 1;
        NBTType type = compound.getType(key);
        Object singleObject = object[0];

        if (singleObject instanceof Boolean && isSingle) {
            compound.setBoolean(key, ((Boolean) singleObject));

        } else if (singleObject instanceof String && (type == NBTType.NBTTagString || custom && isSingle)) {
            compound.setString(key, ((String) singleObject));

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagByte || (custom && isSingle && MathUtil.isByte(singleObject)))) {
            compound.setByte(key, ((Number) singleObject).byteValue());

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagShort || (custom && isSingle && MathUtil.isShort(singleObject)))) {
            compound.setShort(key, ((Number) singleObject).shortValue());

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagInt || (custom && isSingle && MathUtil.isInt(singleObject)))) {
            compound.setInteger(key, ((Number) singleObject).intValue());

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagLong || (custom && isSingle && singleObject instanceof Long))) {
            compound.setLong(key, ((Number) singleObject).longValue());

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagFloat || (custom && isSingle && MathUtil.isFloat(singleObject)))) {
            compound.setFloat(key, ((Number) singleObject).floatValue());

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagDouble || (custom && isSingle && singleObject instanceof Double))) {
            compound.setDouble(key, ((Number) singleObject).doubleValue());

        } else if ((type == NBTType.NBTTagCompound || (custom && isSingle)) && singleObject instanceof NBTCompound) {
            NBTCompound comp;
            if (custom) {
                comp = compound.addCompound(key);
            } else {
                comp = compound.getCompound(key);
                for (String compKey : comp.getKeys()) {
                    comp.removeKey(compKey);
                }
            }
            comp.mergeCompound(((NBTCompound) singleObject));

        } else if (type == NBTType.NBTTagList || (custom && !isSingle && !(object instanceof Integer[]) && !(object instanceof Byte[]))) {
            if (MathUtil.isInt(singleObject)) {
                NBTList<Integer> list = compound.getIntegerList(key);
                list.clear();
                for (Object o : object) {
                    list.add(((Number) o).intValue());
                }
            } else if (singleObject instanceof Long) {
                NBTList<Long> list = compound.getLongList(key);
                list.clear();
                for (Object o : object) {
                    list.add(((Number) o).longValue());
                }
            } else if (MathUtil.isFloat(singleObject)) {
                NBTList<Float> list = compound.getFloatList(key);
                list.clear();
                for (Object o : object) {
                    list.add(((Number) o).floatValue());
                }
            } else if (singleObject instanceof Double) {
                NBTList<Double> list = compound.getDoubleList(key);
                list.clear();
                for (Object o : object) {
                    list.add(((Number) o).doubleValue());
                }
            } else if (singleObject instanceof NBTCompound) {
                NBTCompoundList list = compound.getCompoundList(key);
                list.clear();
                for (Object o : object) {
                    list.addCompound(((NBTCompound) o));
                }
            } else if (singleObject instanceof String) {
                NBTList<String> list = compound.getStringList(key);
                list.clear();
                for (Object o : object) {
                    list.add((String) o);
                }
            }
        } else if (singleObject instanceof Number && (type == NBTType.NBTTagByteArray || object instanceof Byte[])) {
            byte[] n = new byte[object.length];
            for (int i = 0; i < object.length; i++) {
                n[i] = ((Number) object[i]).byteValue();
            }
            compound.setByteArray(key, n);

        } else if (singleObject instanceof Number && (type == NBTType.NBTTagIntArray || object instanceof Integer[])) {
            int[] n = new int[object.length];
            for (int i = 0; i < object.length; i++) {
                n[i] = ((Number) object[i]).intValue();
            }
            compound.setIntArray(key, n);

        } else {
            Util.skriptError("Other-> KEY: " + key + " VALUE: " + singleObject + " VALUE-CLASS: " + object.getClass() + " TYPE: " + type);
        }
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
                if (compound.getIntArray(tag).length == 4) {
                    UUID uuid = compound.getUUID(tag);
                    if (uuid != null) {
                        return uuid;
                    }
                }
                List<Integer> intArray = new ArrayList<>();
                for (int i : compound.getIntArray(tag)) {
                    intArray.add(i);
                }
                return intArray;
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
                return compound.getCompound(tag);
            case NBTTagList:
                List<Object> list = new ArrayList<>();
                list.addAll(compound.getCompoundList(tag));
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
     * Utility method to check if a block is actually a block tile
     *
     * @param blockState State to check
     * @return True if block state is actually a tile
     */
    public static boolean isTileEntity(BlockState blockState) {
        if (HAS_PERSISTENCE) {
            return blockState instanceof TileState;
        } else {
            // Hacky method to check if a BlockState is actually a TileState on legacy versions
            return !blockState.getClass().getName().endsWith("CraftBlockState");
        }
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
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
