package com.shanebeestudios.skbee.api.NBT;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main NBT api for SkBee
 */
public class NBTApi {

    @SuppressWarnings("ConstantConditions")
    public static final boolean SUPPORTS_BLOCK_NBT = PersistentDataHolder.class.isAssignableFrom(Chunk.class);
    private final Config CONFIG;
    private final boolean ENABLED;

    public NBTApi() {
        CONFIG = SkBee.getPlugin().getPluginConfig();
        ENABLED = forceLoadNBT();
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
        Util.skriptError("&cInvalid NBT: &7'&b" + error + "&7'&c");
        if (exception == null) return;

        if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
            exception.printStackTrace();
        } else {
            Util.skriptError("&cCause: &e" + exception.getMessage());
        }
    }

    public static NBTCompound getNestedCompound(String tag, NBTCompound compound) {
        if (compound == null) return null;
        if (tag.contains(";")) {
            String[] splits = tag.split(";(?=(([^\\\"]*\\\"){2})*[^\\\"]*$)");
            for (int i = 0; i < splits.length - 1; i++) {
                String split = splits[i];
                compound = compound.getOrCreateCompound(split);
            }
        }
        return compound;
    }

    public static String getNestedTag(String tag) {
        if (tag.contains(";")) {
            String[] splits = tag.split(";(?=(([^\\\"]*\\\"){2})*[^\\\"]*$)");
            return splits[splits.length - 1];
        }
        return tag;
    }

    private boolean forceLoadNBT() {
        Util.log("&aLoading NBTApi...");
        try {
            NBTItem loadingItem = new NBTItem(new ItemStack(Material.STONE));
            loadingItem.mergeCompound(new NBTContainer("{}"));
        } catch (Exception | ExceptionInInitializerError ignore) {
            Util.log("&cFailed to load NBTApi!");
            return false;
        }
        Util.log("&aSuccessfully loaded NBTApi!");
        return true;
    }

    /**
     * Check if NBTApi is enabled
     * <p>This will fail if NBT_API is not available on this server version</p>
     *
     * @return True if enabled, otherwise false
     */
    public boolean isEnabled() {
        return ENABLED;
    }

    public File getFile(String fileName) {
        fileName = !fileName.endsWith(".dat") && !fileName.endsWith(".nbt") ? fileName + ".dat" : fileName;
        return new File(fileName);
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
                ItemType itemType = (ItemType) object;
                ItemStack stack = itemType.getItem().getRandom();
                if (stack == null || stack.getType() == Material.AIR) return object;

                NBTItem nbtItemType = new NBTItem(stack);
                nbtItemType.mergeCompound(new NBTContainer(value));
                ItemMeta itemMeta = nbtItemType.getItem().getItemMeta();
                if (itemMeta == null) return object;
                itemType.setItemMeta(nbtItemType.getItem().getItemMeta());
                return itemType;
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
                    nbtCompound.removeKey("custom");
                }
                nbtEntity.mergeCompound(nbtCompound);
                return object;
            case BLOCK:
                Block block = (Block) object;
                BlockState blockState = block.getState();

                if (blockState instanceof TileState) {
                    NBTCustomTileEntity nbtBlock = new NBTCustomTileEntity((blockState));
                    NBTCompound updated = new NBTContainer(value);
                    if (updated.hasKey("custom")) {
                        NBTCompound custom = nbtBlock.getCustomNBT();
                        custom.mergeCompound(updated.getCompound("custom"));
                        updated.removeKey("custom");
                    }
                    nbtBlock.mergeCompound(updated);
                    block.getState().update(true, false);
                } else if (SUPPORTS_BLOCK_NBT) {
                    NBTCustomBlock nbtCustomBlock = new NBTCustomBlock(block);
                    nbtCustomBlock.getData().mergeCompound(new NBTContainer(value));
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
                ItemType itemType = ((ItemType) object);
                ItemStack itemStack = new ItemStack(itemType.getMaterial());
                NBTItem nbtItemType = new NBTItem(itemStack);
                nbtItemType.mergeCompound(new NBTContainer(value));
                itemType.setItemMeta(nbtItemType.getItem().getItemMeta());
                return itemType;
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
            compound = getNestedCompound(tag, compound);
            key = getNestedTag(tag);
        }
        compound.removeKey(key);
    }

    public void setTag(@NotNull String tag, @NotNull NBTCompound nbtCompound, @NotNull Object[] object, NBTCustomType type) {
        NBTCompound compound = nbtCompound;
        String key = tag;
        if (tag.contains(";")) {
            String[] splits = tag.split(";(?=(([^\\\"]*\\\"){2})*[^\\\"]*$)");
            for (int i = 0; i < splits.length - 1; i++) {
                String split = splits[i];
                compound = compound.getOrCreateCompound(split);
            }
            key = splits[splits.length - 1];
        }

        Object singleObject = object[0];
        switch (type) {
            case NBTTagByte:
                if (singleObject instanceof Number) {
                    byte b = ((Number) singleObject).byteValue();
                    compound.setByte(key, b);
                }
                break;
            case NBTTagShort:
                if (singleObject instanceof Number) {
                    short s = ((Number) singleObject).shortValue();
                    compound.setShort(key, s);
                }
                break;
            case NBTTagInt:
                if (singleObject instanceof Number) {
                    int i = ((Number) singleObject).intValue();
                    compound.setInteger(key, i);
                }
                break;

            case NBTTagLong:
                if (singleObject instanceof Number) {
                    long l = ((Number) singleObject).longValue();
                    compound.setLong(key, l);
                }
                break;
            case NBTTagFloat:
                if (singleObject instanceof Number) {
                    float f = ((Number) singleObject).floatValue();
                    compound.setFloat(key, f);
                }
                break;
            case NBTTagDouble:
                if (singleObject instanceof Number) {
                    double d = ((Number) singleObject).doubleValue();
                    compound.setDouble(key, d);
                }
                break;
            case NBTTagByteArray:
                if (singleObject instanceof Number) {
                    byte[] ba = new byte[object.length];
                    for (int i = 0; i < object.length; i++) {
                        ba[i] = ((Number) object[i]).byteValue();
                    }
                    compound.setByteArray(key, ba);
                }
                break;
            case NBTTagIntArray:
                if (singleObject instanceof Number) {
                    int[] ia = new int[object.length];
                    for (int i = 0; i < object.length; i++) {
                        ia[i] = ((Number) object[i]).intValue();
                    }
                    compound.setIntArray(key, ia);
                }
                break;
            case NBTTagString:
                if (singleObject instanceof String) {
                    String s = ((String) singleObject);
                    compound.setString(key, s);
                }
                break;
            case NBTTagCompound:
                if (singleObject instanceof NBTCompound) {
                    NBTCompound nbt = (NBTCompound) singleObject;
                    compound.removeKey(key);
                    try {
                        NBTCompound newCompound = compound.getOrCreateCompound(key);
                        if (newCompound != null) {
                            newCompound.mergeCompound(nbt);
                        }
                    } catch (NbtApiException ignore) {
                    }
                }
            case NBTTagIntList:
                if (singleObject instanceof Number) {
                    NBTList<Integer> intList = compound.getIntegerList(key);
                    intList.clear();
                    for (Object o : object)
                        if (o instanceof Number)
                            intList.add(((Number) o).intValue());
                }
                break;
            case NBTTagLongList:
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(key);
                    longList.clear();
                    for (Object o : object)
                        if (o instanceof Number)
                            longList.add(((Number) o).longValue());
                }
                break;
            case NBTTagFloatList:
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(key);
                    floatList.clear();
                    for (Object o : object)
                        if (o instanceof Number)
                            floatList.add(((Number) o).floatValue());
                }
                break;
            case NBTTagDoubleList:
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(key);
                    doubleList.clear();
                    for (Object o : object)
                        if (o instanceof Number)
                            doubleList.add(((Number) o).doubleValue());
                }
                break;
            case NBTTagStringList:
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(key);
                    stringList.clear();
                    for (Object o : object)
                        if (o instanceof String)
                            stringList.add(((String) o));
                }
                break;
            case NBTTagCompoundList:
                if (singleObject instanceof NBTCompound) {
                    NBTCompoundList compoundList = compound.getCompoundList(key);
                    compoundList.clear();
                    for (Object o : object) {
                        if (o instanceof NBTCompound)
                            compoundList.addCompound(((NBTCompound) o));
                    }
                }
                break;
        }
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
            compound = getNestedCompound(key, compound);
            key = getNestedTag(key);
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
                comp = compound.getOrCreateCompound(key);
            } else {
                comp = compound.getCompound(key);
                for (String compKey : comp.getKeys()) {
                    comp.removeKey(compKey);
                }
            }
            if (comp != null) {
                comp.mergeCompound(((NBTCompound) singleObject));
            }

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
     * <p>Sub-compounds can be split using ';',
     * example tag: "custom;sub"</p>
     *
     * @param tag      Tag to check for
     * @param compound NBT to grab tag from
     * @return Object from the NBT string
     */
    public Object getTag(String tag, NBTCompound compound) {
        if (compound == null) return null;
        if (tag.contains(";")) {
            compound = getNestedCompound(tag, compound);
            tag = getNestedTag(tag);
        }
        NBTCustomType type = NBTCustomType.getByTag(compound, tag);
        if (type == null) {
            return null;
        }
        // Small fix for "custom" tags not being real tags in an NBT compound and showing as NBTTagEnd
        if (type == NBTCustomType.NBTTagEnd && compound instanceof NBTCustom && tag.equalsIgnoreCase("custom")) {
            type = NBTCustomType.NBTTagCompound;
        }
        return getTag(tag, compound, type);
    }

    /**
     * Get a specific tag from an NBT string
     * <p>Sub-compounds can be split using ';',
     * example tag: "custom;sub"</p>
     *
     * @param tag      Tag to check for
     * @param compound NBT to grab tag from
     * @param type     Type of NBT tag
     * @return Object from the NBT string
     */
    public Object getTag(String tag, NBTCompound compound, NBTCustomType type) {
        if (compound == null) return null;
        if (tag.contains(";")) {
            compound = getNestedCompound(tag, compound);
            tag = getNestedTag(tag);
        }

        switch (type) {
            case NBTTagString:
                return compound.getString(tag);
            case NBTTagByteArray:
                List<Byte> byteArray = new ArrayList<>();
                for (byte i : compound.getByteArray(tag)) {
                    byteArray.add(i);
                }
                return byteArray;
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
            case NBTTagByte:
                return compound.getByte(tag);
            case NBTTagShort:
                return compound.getShort(tag);
            case NBTTagInt:
                return compound.getInteger(tag);
            case NBTTagLong:
                return compound.getLong(tag);
            case NBTTagFloat:
                return compound.getFloat(tag);
            case NBTTagDouble:
                return compound.getDouble(tag);
            case NBTTagEnd:
                return null;
            case NBTTagCompound:
                return compound.getOrCreateCompound(tag);
            case NBTTagCompoundList:
                return new ArrayList<>(compound.getCompoundList(tag));
            case NBTTagStringList:
                return new ArrayList<>(compound.getStringList(tag));
            case NBTTagDoubleList:
                return new ArrayList<>(compound.getDoubleList(tag));
            case NBTTagFloatList:
                return new ArrayList<>(compound.getFloatList(tag));
            case NBTTagIntList:
                return new ArrayList<>(compound.getIntegerList(tag));
            case NBTTagLongList:
                return new ArrayList<>(compound.getLongList(tag));
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
