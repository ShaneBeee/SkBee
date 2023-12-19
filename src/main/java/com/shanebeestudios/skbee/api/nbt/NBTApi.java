package com.shanebeestudios.skbee.api.nbt;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Pair;
import com.shanebeestudios.skbee.api.util.Util;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTFile;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
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
    private static final boolean SUPPORTS_BLOCK_NBT = PersistentDataHolder.class.isAssignableFrom(Chunk.class);
    private static boolean ENABLED;
    private static boolean DEBUG;

    /**
     * Initialize this NBT API
     * <br>
     * This should NOT be used by other plugins.
     */
    public static void initializeAPI() {
        Util.log("&aLoading NBTApi...");
        MinecraftVersion version = MinecraftVersion.getVersion();
        if (version == MinecraftVersion.UNKNOWN) {
            Util.log("&cFailed to load NBTApi!");
            ENABLED = false;
        } else {
            Util.log("&aSuccessfully loaded NBTApi!");
            // Failsafe to make sure API is properly loaded each time
            // This is to prevent an error when unloading/saving vars
            // noinspection ResultOfMethodCallIgnored
            new NBTContainer("{a:1}").toString();
            ENABLED = true;
        }
        DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;
    }

    /**
     * Check if NBTApi is enabled
     * <p>This will fail if NBT_API is not available on this server version</p>
     *
     * @return True if enabled, otherwise false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isEnabled() {
        return ENABLED;
    }

    public static boolean supportsBlockNBT() {
        return SUPPORTS_BLOCK_NBT;
    }

    /**
     * Validate an NBT string
     * <br>
     * If the NBT is invalid, and error will be thrown.
     *
     * @param nbtString NBT string to validate
     * @return NBTCompound if NBT string is valid, otherwise null
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static NBTCompound validateNBT(String nbtString) {
        if (nbtString == null) return null;
        NBTCompound compound;
        try {
            compound = new NBTContainer(nbtString);
        } catch (Exception ex) {
            Util.skriptError("&cInvalid NBT: &7'&b%s&7'&c", nbtString);

            if (DEBUG) {
                ex.printStackTrace();
            } else {
                // 3 deep to get the Mojang CommandSyntaxException
                String cause = ex.getCause().getCause().getCause().toString();
                cause = cause.replace("com.mojang.brigadier.exceptions.CommandSyntaxException", "MalformedNBT");
                Util.skriptError("&cMessage: &e%s", cause);
            }
            return null;
        }
        return compound;
    }

    @Nullable
    public static Pair<String, NBTCompound> getNestedCompound(String tag, NBTCompound compound) {
        if (compound == null || tag == null) return null;
        if (tag.contains(";")) {
            String subTag = tag.substring(0, tag.lastIndexOf(";")).replace(".", "\\.").replace(";", ".");
            compound = (NBTCompound) compound.resolveOrCreateCompound(subTag);
            tag = getNestedTag(tag);
        }
        if (compound == null || tag == null) return null;
        return new Pair<>(tag, compound);
    }

    public static String getNestedTag(String tag) {
        if (tag.contains(";")) {
            String[] splits = tag.split(";(?=(([^\"]*\"){2})*[^\"]*$)");
            return splits[splits.length - 1];
        }
        return tag;
    }

    public static boolean hasTag(NBTCompound compound, String tag) {
        if (!tag.contains(";")) {
            return compound.hasTag(tag);
        }
        String subTag = tag.substring(0, tag.lastIndexOf(";")).replace(".", "\\.").replace(";", ".");
        NBTCompound subCompound = (NBTCompound) compound.resolveCompound(subTag);
        return subCompound != null && subCompound.hasTag(getNestedTag(tag));
    }

    /**
     * Get an {@link NBTFile}
     *
     * @param fileName Name of file
     * @return new NBTFile
     */
    public static NBTFile getNBTFile(String fileName) {
        fileName = !fileName.endsWith(".dat") && !fileName.endsWith(".nbt") ? fileName + ".nbt" : fileName;
        try {
            return new NBTFile(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get an {@link NBTCustomOfflinePlayer}
     * <p>This internally just creates a new NBTFile from the player data folder</p>
     *
     * @param offlinePlayer OfflinePlayer to grab nbt for
     * @return NBTCustomOfflinePlayer
     */
    public static NBTCustomOfflinePlayer getNBTOfflinePlayer(OfflinePlayer offlinePlayer) {
        // Only return if player data file exists
        if (!offlinePlayer.hasPlayedBefore()) return null;
        try {
            return new NBTCustomOfflinePlayer(offlinePlayer);
        } catch (IOException ignore) {
            return null;
        }
    }

    /**
     * Check if an NBT File already exists
     *
     * @param fileName Name of file
     * @return true if file exists else false
     */
    public static boolean nbtFileExists(String fileName) {
        fileName = !fileName.endsWith(".dat") && !fileName.endsWith(".nbt") ? fileName + ".nbt" : fileName;
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Merge an {@link NBTCompound} into an {@link ItemType}
     *
     * @param itemType    ItemType to add NBT to
     * @param nbtCompound NBT to add to ItemType
     * @return ItemType with NBT merged into
     */
    public static ItemType getItemTypeWithNBT(ItemType itemType, NBTCompound nbtCompound) {
        NBTContainer itemNBT = NBTItem.convertItemtoNBT(itemType.getRandom());

        // Full NBT
        if (nbtCompound.hasTag("tag")) {
            if (nbtCompound.hasTag("id") && !itemNBT.getString("id").equalsIgnoreCase(nbtCompound.getString("id"))) {
                // NBT compounds not the same item
                return itemType;
            }
            itemNBT.mergeCompound(nbtCompound);
        } else {
            // Tag portion of NBT
            itemNBT.getOrCreateCompound("tag").mergeCompound(nbtCompound);
        }
        ItemStack newItemStack = NBTItem.convertNBTtoItem(itemNBT);
        return new ItemType(newItemStack);
    }

    /**
     * Delete a tag from an {@link NBTCompound}
     *
     * @param tag      Tag to delete
     * @param compound Compound to remove tag from
     */
    public static void deleteTag(@NotNull String tag, @NotNull NBTCompound compound) {
        if (tag.equalsIgnoreCase("custom") && compound instanceof NBTCustom nbtCustom) {
            nbtCustom.deleteCustomNBT();
            return;
        }
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return;

        tag = nestedCompound.first();
        compound = nestedCompound.second();
        compound.removeKey(tag);
    }

    /**
     * Set a specific tag of an {@link NBTCompound}
     *
     * @param tag      Tag that will be set
     * @param compound Compound to change
     * @param object   Value of tag to set to
     * @param type     Type of tag to set
     */
    @SuppressWarnings({"RegExpRedundantEscape", "ListRemoveInLoop"})
    public static void setTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull Object[] object, NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return;

        compound = nestedCompound.second();
        tag = nestedCompound.first();

        Object singleObject = object[0];
        switch (type) {
            case NBTTagByte:
                if (singleObject instanceof Boolean bool) {
                    compound.setByte(tag, (byte) (bool ? 1 : 0));
                } else if (singleObject instanceof Number number) {
                    compound.setByte(tag, number.byteValue());
                }
                break;
            case NBTTagShort:
                if (singleObject instanceof Number number) {
                    compound.setShort(tag, number.shortValue());
                }
                break;
            case NBTTagInt:
                if (singleObject instanceof Number number) {
                    compound.setInteger(tag, number.intValue());
                }
                break;

            case NBTTagLong:
                if (singleObject instanceof Number number) {
                    compound.setLong(tag, number.longValue());
                }
                break;
            case NBTTagFloat:
                if (singleObject instanceof Number number) {
                    compound.setFloat(tag, number.floatValue());
                }
                break;
            case NBTTagDouble:
                if (singleObject instanceof Number number) {
                    compound.setDouble(tag, number.doubleValue());
                }
                break;
            case NBTTagByteArray:
                if (singleObject instanceof Number) {
                    byte[] ba = new byte[object.length];
                    for (int i = 0; i < object.length; i++) {
                        ba[i] = ((Number) object[i]).byteValue();
                    }
                    compound.setByteArray(tag, ba);
                }
                break;
            case NBTTagIntArray:
                if (singleObject instanceof Number) {
                    int[] ia = new int[object.length];
                    for (int i = 0; i < object.length; i++) {
                        ia[i] = ((Number) object[i]).intValue();
                    }
                    compound.setIntArray(tag, ia);
                }
                break;
            case NBTTagUUID:
                UUID uuid = null;
                int[] ints = new int[0];
                if (singleObject instanceof String string) {
                    try {
                        uuid = UUID.fromString(string);
                    } catch (IllegalArgumentException ignore) {
                    }
                } else if (singleObject instanceof UUID u) {
                    uuid = u;
                } else if (singleObject instanceof Entity entity) {
                    uuid = entity.getUniqueId();
                } else if (singleObject instanceof OfflinePlayer offlinePlayer) {
                    uuid = offlinePlayer.getUniqueId();
                } else if (singleObject instanceof Number) {
                    ints = new int[object.length];
                    for (int i = 0; i < object.length; i++) {
                        ints[i] = ((Number) object[i]).intValue();
                    }
                }
                if (uuid != null) {
                    //ints = Util.uuidToIntArray(uuid);
                    compound.setUUID(tag, uuid);
                    break;
                }
                if (ints.length > 0) {
                    compound.setIntArray(tag, ints);
                }
                break;
            case NBTTagString:
                if (singleObject instanceof String string) {
                    compound.setString(tag, string);
                } else {
                    compound.setString(tag, Classes.toString(singleObject));
                }
                break;
            case NBTTagCompound:
                if (singleObject instanceof NBTCompound nbt) {
                    // Create a copy of the nbt we're going to merge in
                    NBTContainer copy = new NBTContainer();
                    copy.mergeCompound(nbt);

                    NBTCompound subCompound = compound.getOrCreateCompound(tag);
                    // While this shouldn't happen, the API likes to do this for blocks
                    if (subCompound == null) return;
                    // Clear out old data before merging
                    subCompound.clearNBT();
                    subCompound.mergeCompound(copy);
                }
            case NBTTagIntList:
                if (singleObject instanceof Number) {
                    NBTList<Integer> intList = compound.getIntegerList(tag);
                    int size = intList.size();
                    for (Object o : object)
                        if (o instanceof Number number)
                            intList.add(number.intValue());

                    for (int i = 0; i < size; i++) {
                        intList.remove(0);
                    }
                }
                break;
            case NBTTagLongList:
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(tag);
                    int size = longList.size();
                    for (Object o : object)
                        if (o instanceof Number number)
                            longList.add(number.longValue());

                    for (int i = 0; i < size; i++) {
                        longList.remove(0);
                    }
                }
                break;
            case NBTTagFloatList:
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(tag);
                    int size = floatList.size();
                    for (Object o : object)
                        if (o instanceof Number number)
                            floatList.add(number.floatValue());

                    for (int i = 0; i < size; i++) {
                        floatList.remove(0);
                    }
                }
                break;
            case NBTTagDoubleList:
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(tag);
                    int size = doubleList.size();
                    for (Object o : object)
                        if (o instanceof Number number)
                            doubleList.add(number.doubleValue());

                    for (int i = 0; i < size; i++) {
                        doubleList.remove(0);
                    }
                }
                break;
            case NBTTagStringList:
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(tag);
                    int size = stringList.size();
                    for (Object o : object)
                        if (o instanceof String string)
                            stringList.add(string);
                    for (int i = 0; i < size; i++) {
                        stringList.remove(0);
                    }
                }
                break;
            case NBTTagCompoundList:
                if (singleObject instanceof NBTCompound) {
                    NBTCompoundList compoundList = compound.getCompoundList(tag);
                    int size = compoundList.size();
                    for (Object o : object) {
                        if (o instanceof NBTCompound comp)
                            compoundList.addCompound(comp);
                    }

                    for (int i = 0; i < size; i++) {
                        compoundList.remove(0);
                    }
                }
                break;
        }
    }

    /**
     * Set a specific tag of an {@link NBTCompound}
     *
     * @param tag      Tag that will be set
     * @param compound Compound to change
     * @param object   Value of tag to set to
     */
    public static void setTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull Object[] object) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

        Object singleObject = object[0];
        NBTCustomType customType = NBTCustomType.NBTTagEnd;

        if (!compound.hasTag(tag)) {
            if (object.length == 1) {
                if (singleObject instanceof Boolean) customType = NBTCustomType.NBTTagByte;
                else if (singleObject instanceof String) customType = NBTCustomType.NBTTagString;
                else if (singleObject instanceof Long l) {
                    if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE) customType = NBTCustomType.NBTTagInt;
                    else customType = NBTCustomType.NBTTagLong;
                } else if (singleObject instanceof Double d) {
                    if (d < Float.MAX_VALUE && d > Float.MIN_VALUE) customType = NBTCustomType.NBTTagFloat;
                    else customType = NBTCustomType.NBTTagDouble;
                } else if (singleObject instanceof NBTCompound) customType = NBTCustomType.NBTTagCompound;
                else if (singleObject instanceof OfflinePlayer || singleObject instanceof Entity)
                    customType = NBTCustomType.NBTTagUUID;
                else customType = NBTCustomType.NBTTagString;
            } else {
                if (singleObject instanceof String) customType = NBTCustomType.NBTTagStringList;
                else if (singleObject instanceof Long l) {
                    if (l < Integer.MAX_VALUE && l > Integer.MIN_VALUE) customType = NBTCustomType.NBTTagIntList;
                    else customType = NBTCustomType.NBTTagLongList;
                } else if (singleObject instanceof Double d) {
                    if (d < Float.MAX_VALUE && d > Float.MIN_VALUE) customType = NBTCustomType.NBTTagFloatList;
                    else customType = NBTCustomType.NBTTagDoubleList;
                } else if (singleObject instanceof NBTCompound) customType = NBTCustomType.NBTTagCompoundList;
            }
        } else {
            customType = NBTCustomType.getByTag(compound, tag);
        }
        setTag(tag, compound, object, customType);
    }

    /**
     * Add a value to a tag
     *
     * @param tag      Tag to modify
     * @param compound Compound to modify
     * @param object   Value to add
     * @param type     Type of tag
     */
    @SuppressWarnings("RegExpRedundantEscape")
    public static void addToTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull Object[] object, NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

        // If the tag type doesn't match, return (TagEnd excluded as this means the tag isn't set)
        NBTCustomType byTag = NBTCustomType.getByTag(compound, tag);
        if (byTag != NBTCustomType.NBTTagEnd && byTag != type) return;

        Object singleObject = object[0];
        switch (type) {
            case NBTTagByte -> {
                if (singleObject instanceof Number number) {
                    compound.setByte(tag, (byte) (compound.getByte(tag) + number.byteValue()));
                }
            }
            case NBTTagShort -> {
                if (singleObject instanceof Number number) {
                    compound.setShort(tag, (short) (compound.getShort(tag) + number.shortValue()));
                }
            }
            case NBTTagInt -> {
                if (singleObject instanceof Number number) {
                    compound.setInteger(tag, compound.getInteger(tag) + number.intValue());
                }
            }
            case NBTTagLong -> {
                if (singleObject instanceof Number number) {
                    compound.setLong(tag, compound.getLong(tag) + number.longValue());
                }
            }
            case NBTTagFloat -> {
                if (singleObject instanceof Number number) {
                    compound.setFloat(tag, compound.getFloat(tag) + number.floatValue());
                }
            }
            case NBTTagDouble -> {
                if (singleObject instanceof Number number) {
                    compound.setDouble(tag, compound.getDouble(tag) + number.doubleValue());
                }
            }
            case NBTTagByteArray -> {
                if (singleObject instanceof Number) {
                    byte[] byteArray = compound.getByteArray(tag);
                    for (Object o : object) {
                        if (o instanceof Number number) {
                            byteArray = ArrayUtils.add(byteArray, number.byteValue());
                        }
                    }
                    compound.setByteArray(tag, byteArray);
                }
            }
            case NBTTagIntArray -> {
                if (singleObject instanceof Number) {
                    int[] intArray = compound.getIntArray(tag);
                    for (Object o : object) {
                        if (o instanceof Number number) {
                            intArray = ArrayUtils.add(intArray, number.intValue());
                        }
                    }
                    compound.setIntArray(tag, intArray);
                }
            }
            case NBTTagIntList -> {
                if (singleObject instanceof Number) {
                    NBTList<Integer> intList = compound.getIntegerList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            intList.add(number.intValue());
                }
            }
            case NBTTagLongList -> {
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            longList.add(number.longValue());
                }
            }
            case NBTTagFloatList -> {
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            floatList.add(number.floatValue());
                }
            }
            case NBTTagDoubleList -> {
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            doubleList.add(number.doubleValue());
                }
            }
            case NBTTagStringList -> {
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(tag);
                    for (Object o : object)
                        if (o instanceof String string)
                            stringList.add(string);
                }
            }
            case NBTTagCompoundList -> {
                if (singleObject instanceof NBTCompound) {
                    NBTCompoundList compoundList = compound.getCompoundList(tag);
                    for (Object o : object) {
                        if (o instanceof NBTCompound comp)
                            compoundList.addCompound(comp);
                    }
                }
            }
        }
    }

    /**
     * Remove a value from a tag
     *
     * @param tag      Tag to modify
     * @param compound Compound to modify
     * @param object   Value to remove
     * @param type     Type of tag
     */
    @SuppressWarnings("RegExpRedundantEscape")
    public static void removeFromTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull Object[] object, NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

        // If tag type does not match, return!
        if (NBTCustomType.getByTag(compound, tag) != type) return;

        Object singleObject = object[0];
        switch (type) {
            case NBTTagByte -> {
                if (singleObject instanceof Number number) {
                    compound.setByte(tag, (byte) (compound.getByte(tag) - number.byteValue()));
                }
            }
            case NBTTagShort -> {
                if (singleObject instanceof Number number) {
                    compound.setShort(tag, (short) (compound.getShort(tag) - number.shortValue()));
                }
            }
            case NBTTagInt -> {
                if (singleObject instanceof Number number) {
                    compound.setInteger(tag, compound.getInteger(tag) - number.intValue());
                }
            }
            case NBTTagLong -> {
                if (singleObject instanceof Number number) {
                    compound.setLong(tag, compound.getLong(tag) - number.longValue());
                }
            }
            case NBTTagFloat -> {
                if (singleObject instanceof Number number) {
                    compound.setFloat(tag, compound.getFloat(tag) - number.floatValue());
                }
            }
            case NBTTagDouble -> {
                if (singleObject instanceof Number number) {
                    compound.setDouble(tag, compound.getDouble(tag) - number.doubleValue());
                }
            }
            case NBTTagByteArray -> {
                if (singleObject instanceof Number) {
                    byte[] byteArray = compound.getByteArray(tag);

                    for (Object o : object) {
                        if (o instanceof Number number) {
                            int index = ArrayUtils.indexOf(byteArray, number.byteValue());
                            byteArray = ArrayUtils.remove(byteArray, index);
                        }
                    }
                    compound.setByteArray(tag, byteArray);
                }
            }
            case NBTTagIntArray -> {
                if (singleObject instanceof Number) {
                    int[] intArray = compound.getIntArray(tag);

                    for (Object o : object) {
                        if (o instanceof Number number) {
                            int index = ArrayUtils.indexOf(intArray, number.intValue());
                            intArray = ArrayUtils.remove(intArray, index);
                        }
                    }
                    compound.setIntArray(tag, intArray);
                }
            }
            case NBTTagIntList -> {
                if (singleObject instanceof Number) {
                    NBTList<Integer> intList = compound.getIntegerList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            intList.remove(number.intValue());
                }
            }
            case NBTTagLongList -> {
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            longList.remove(number.longValue());
                }
            }
            case NBTTagFloatList -> {
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            floatList.remove(number.floatValue());
                }
            }
            case NBTTagDoubleList -> {
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            doubleList.remove(number.doubleValue());
                }
            }
            case NBTTagStringList -> {
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(tag);
                    for (Object o : object)
                        if (o instanceof String string)
                            stringList.remove(string);
                }
            }
            case NBTTagCompoundList -> {
                // Not sure if possible, leave for now
                // Error has been added in ExprTagOfNBT
            }
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
    public static Object getTag(String tag, NBTCompound compound) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return null;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

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
    public static Object getTag(String tag, NBTCompound compound, NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound);
        if (nestedCompound == null) return null;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

        switch (type) {
            case NBTTagString -> {
                return compound.getString(tag);
            }
            case NBTTagByteArray -> {
                List<Byte> byteArray = new ArrayList<>();
                for (byte i : compound.getByteArray(tag)) {
                    byteArray.add(i);
                }
                return byteArray;
            }
            case NBTTagIntArray -> {
                List<Integer> intArray = new ArrayList<>();
                for (int i : compound.getIntArray(tag)) {
                    intArray.add(i);
                }
                return intArray;
            }
            case NBTTagUUID -> {
                try {
                    UUID uuid = compound.getUUID(tag);
                    if (uuid != null) {
                        return uuid.toString();
                    }
                } catch (NbtApiException ignore) {
                }
            }
            case NBTTagByte -> {
                return compound.getByte(tag);
            }
            case NBTTagShort -> {
                return compound.getShort(tag);
            }
            case NBTTagInt -> {
                return compound.getInteger(tag);
            }
            case NBTTagLong -> {
                return compound.getLong(tag);
            }
            case NBTTagFloat -> {
                return compound.getFloat(tag);
            }
            case NBTTagDouble -> {
                return compound.getDouble(tag);
            }
            case NBTTagEnd -> {
                return null;
            }
            case NBTTagCompound -> {
                if (compound.hasTag(tag)) {
                    if (compound.getType(tag) == NBTType.NBTTagCompound) {
                        return compound.getCompound(tag);
                    }
                } else {
                    return compound.getOrCreateCompound(tag);
                }
            }
            case NBTTagCompoundList -> {
                return new ArrayList<>(compound.getCompoundList(tag));
            }
            case NBTTagStringList -> {
                return new ArrayList<>(compound.getStringList(tag));
            }
            case NBTTagDoubleList -> {
                return new ArrayList<>(compound.getDoubleList(tag));
            }
            case NBTTagFloatList -> {
                return new ArrayList<>(compound.getFloatList(tag));
            }
            case NBTTagIntList -> {
                return new ArrayList<>(compound.getIntegerList(tag));
            }
            case NBTTagLongList -> {
                return new ArrayList<>(compound.getLongList(tag));
            }
            default -> {
                if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG)
                    throw new IllegalArgumentException("Unknown tag type, please let the dev know -> type: " + type);
            }
        }
        return null;
    }

    /**
     * Add NBT to a {@link Block}
     * <br>
     * This will merge an {@link NBTCompound} into the compound of a block
     *
     * @param block    Block to merge NBT into
     * @param compound Compound to merge
     */
    public static void addNBTToBlock(Block block, NBTCompound compound) {
        // We shouldn't be adding NBT to air
        if (block.getType().isAir()) return;
        BlockState blockState = block.getState();
        if (blockState instanceof TileState tileState) {
            NBTCustomTileEntity nbtBlock = new NBTCustomTileEntity(tileState);
            nbtBlock.mergeCompound(compound);
        } else if (SUPPORTS_BLOCK_NBT) {
            NBTCustomBlock nbtCustomBlock = new NBTCustomBlock(block);
            nbtCustomBlock.getData().mergeCompound(compound);
        }
    }

    /**
     * Add NBT to an {@link Entity}
     * <br>
     * This will merge an {@link NBTCompound} into the compound of an entity
     *
     * @param entity   Entity to merge NBT into
     * @param compound Compound to merge
     */
    public static void addNBTToEntity(Entity entity, NBTCompound compound) {
        NBTCustomEntity nbtEntity = new NBTCustomEntity(entity);
        nbtEntity.mergeCompound(compound);
    }

}
