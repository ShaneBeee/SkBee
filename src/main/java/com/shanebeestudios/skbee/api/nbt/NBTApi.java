package com.shanebeestudios.skbee.api.nbt;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Pair;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.config.Config;
import de.tr7zw.changeme.nbtapi.NBT;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
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
@SuppressWarnings("deprecation")
public class NBTApi {

    @SuppressWarnings("ConstantConditions")
    private static boolean ENABLED;

    /**
     * Initialize this NBT API
     * <br>
     * This should NOT be used by other plugins.
     */
    public static void initializeAPI(Config config) {
        Util.log("&aLoading NBTApi...");
        MinecraftVersion version = MinecraftVersion.getVersion();
        if (version == MinecraftVersion.UNKNOWN) {
            if (config.NBT_ALLOW_FORCE_LOAD_UNKNOWN_VERSION) {
                Util.log("&eAttempting to force load NBT-API with an unknown Minecraft version.");
                try {
                    // Failsafe to make sure API is properly loaded each time
                    // This is to prevent an error when unloading/saving vars
                    // noinspection ResultOfMethodCallIgnored
                    new NBTContainer("{a:1}").toString();
                    Util.log("&eNBT-API has loaded and will use recent mappings.");
                    Util.log("&eThings may not work as expected");
                    ENABLED = true;
                } catch (NbtApiException ignore) {
                    Util.log("&cFailed to load NBTApi!");
                    ENABLED = false;
                }
            } else {
                Util.log("&cFailed to load NBTApi!");
                ENABLED = false;
            }
        } else {
            Util.log("&aSuccessfully loaded NBTApi!");
            // Failsafe to make sure API is properly loaded each time
            // This is to prevent an error when unloading/saving vars
            // noinspection ResultOfMethodCallIgnored
            new NBTContainer("{a:1}").toString();
            ENABLED = true;
        }
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

    /**
     * Validate an NBT string
     * <br>
     * If the NBT is invalid, and error will be thrown.
     *
     * @param nbtString NBT string to validate
     * @return NBTCompound if NBT string is valid, otherwise null
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static @Nullable NBTCompound validateNBT(String nbtString) {
        if (nbtString == null) return null;
        NBTCompound compound = (NBTCompound) NBT.createNBTObject();
        try {
            compound.mergeCompound(NBT.parseNBT(nbtString));
        } catch (Exception ex) {
            Util.skriptError("&cInvalid NBT: &7'&b%s&7'&c", nbtString);

            if (SkBee.isDebug()) {
                ex.printStackTrace();
            } else {
                // 3 deep to get the Mojang CommandSyntaxException
                String cause = ex.getCause().getCause().getCause().toString();
                cause = cause.replace("com.mojang.brigadier.exceptions.CommandSyntaxException", "MalformedNBT");
                Util.skriptError("&cMessage: &e%s", cause);
            }
            Util.errorForAdmins("Invalid NBT, please check console for more details.");
            return null;
        }
        return compound;
    }

    @Nullable
    public static Pair<String, NBTCompound> getNestedCompound(String tag, NBTCompound compound, boolean requiresNested) {
        if (compound == null || tag == null) return null;
        if (tag.contains(";")) {
            String subTag = tag.substring(0, tag.lastIndexOf(";")).replace(".", "\\.").replace(";", ".");
            if (requiresNested) {
                compound = (NBTCompound) compound.resolveCompound(subTag);
            } else {
                compound = (NBTCompound) compound.resolveOrCreateCompound(subTag);
            }

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
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, true);
        if (nestedCompound != null) return nestedCompound.second().hasTag(nestedCompound.first());
        return false;
    }

    /**
     * Get the {@link NBTCustomType type} of a tag from a compound
     *
     * @param compound Compound to grab tag from
     * @param tag      Tag to check
     * @return Type of tag
     */
    @Nullable
    public static NBTCustomType getTagType(NBTCompound compound, String tag) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, true);
        if (nestedCompound != null) {
            tag = nestedCompound.first();
            compound = nestedCompound.second();
            return NBTCustomType.getByTag(compound, tag);
        }
        return null;
    }

    /**
     * Get an {@link NBTFile}
     *
     * @param fileName Name of file
     * @return new NBTFile
     */
    public static @Nullable NBTFile getNBTFile(String fileName) {
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
    public static @Nullable NBTCustomOfflinePlayer getNBTOfflinePlayer(OfflinePlayer offlinePlayer) {
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
     * @param custom      Should be stored within the "minecraft:custom_data" component (1.20.5+)
     * @return ItemType with NBT merged into
     */
    public static @Nullable ItemType getItemTypeWithNBT(ItemType itemType, NBTCompound nbtCompound, boolean custom) {
        NBTContainer itemNBT = NBTItem.convertItemtoNBT(itemType.getRandom());

        // Full NBT
        if (nbtCompound.hasTag("components")) {
            if (nbtCompound.hasTag("id") && !itemNBT.getString("id").equalsIgnoreCase(nbtCompound.getString("id"))) {
                // NBT compounds not the same item
                return itemType;
            }
            itemNBT.mergeCompound(nbtCompound);
        } else {
            // Components/Tag portion of NBT
            NBTCompound components = itemNBT.getOrCreateCompound("components");
            if (custom) components = components.getOrCreateCompound("minecraft:custom_data");
            components.mergeCompound(nbtCompound);
        }
        ItemStack newItemStack = NBTItem.convertNBTtoItem(itemNBT);
        if (newItemStack == null) return null;
        return new ItemType(newItemStack);
    }

    /**
     * Delete a tag from an {@link NBTCompound}
     *
     * @param tag      Tag to delete
     * @param compound Compound to remove tag from
     */
    public static void deleteTag(@NotNull String tag, @NotNull NBTCompound compound) {
        if (tag.equalsIgnoreCase("custom") && compound instanceof NBTCustom nbtCustom && !(compound instanceof NBTCustomItemStack)) {
            nbtCustom.deleteCustomNBT();
            return;
        }
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, true);
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
    @SuppressWarnings({"RegExpRedundantEscape", "IfCanBeSwitch"})
    public static void setTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull Object[] object, NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, false);
        if (nestedCompound == null) return;

        compound = nestedCompound.second();
        tag = nestedCompound.first();

        Object singleObject = object[0];
        switch (type) {
            case NBTTagBoolean:
                if (singleObject instanceof Boolean bool) {
                    compound.setBoolean(tag, bool);
                }
                break;
            case NBTTagByte:
                if (singleObject instanceof Number number) {
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
                    compound.setUUID(tag, uuid);
                } else if (ints.length == 4) { // Only allows 4 ints
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
                    intList.clear();
                    for (Object o : object)
                        if (o instanceof Number number)
                            intList.add(number.intValue());
                }
                break;
            case NBTTagLongList:
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(tag);
                    longList.clear();
                    for (Object o : object)
                        if (o instanceof Number number)
                            longList.add(number.longValue());
                }
                break;
            case NBTTagFloatList:
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(tag);
                    floatList.clear();
                    for (Object o : object)
                        if (o instanceof Number number)
                            floatList.add(number.floatValue());
                }
                break;
            case NBTTagDoubleList:
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(tag);
                    doubleList.clear();
                    for (Object o : object)
                        if (o instanceof Number number)
                            doubleList.add(number.doubleValue());
                }
                break;
            case NBTTagStringList:
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(tag);
                    stringList.clear();
                    for (Object o : object)
                        if (o instanceof String string)
                            stringList.add(string);
                }
                break;
            case NBTTagCompoundList:
                if (singleObject instanceof NBTCompound) {
                    NBTCompoundList compoundList = compound.getCompoundList(tag);
                    compoundList.clear();
                    for (Object o : object) {
                        if (o instanceof NBTCompound comp)
                            compoundList.addCompound(comp);
                    }
                }
                break;
        }
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
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, false);
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
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, false);
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
                    if (byteArray == null) return;

                    for (Object o : object) {
                        if (o instanceof Number number) {
                            int index = ArrayUtils.indexOf(byteArray, number.byteValue());
                            byteArray = ArrayUtils.remove(byteArray, index);
                        }
                    }
                    if (byteArray.length > 0) {
                        compound.setByteArray(tag, byteArray);
                    } else {
                        compound.removeKey(tag);
                    }
                }
            }
            case NBTTagIntArray -> {
                if (singleObject instanceof Number) {
                    int[] intArray = compound.getIntArray(tag);
                    if (intArray == null) return;

                    for (Object o : object) {
                        if (o instanceof Number number) {
                            int index = ArrayUtils.indexOf(intArray, number.intValue());
                            intArray = ArrayUtils.remove(intArray, index);
                        }
                    }
                    if (intArray.length > 0) {
                        compound.setIntArray(tag, intArray);
                    } else {
                        compound.removeKey(tag);
                    }
                }
            }
            case NBTTagIntList -> {
                if (singleObject instanceof Number) {
                    NBTList<Integer> intList = compound.getIntegerList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            intList.remove((Object) number.intValue());
                    if (intList.isEmpty()) compound.removeKey(tag);
                }
            }
            case NBTTagLongList -> {
                if (singleObject instanceof Number) {
                    NBTList<Long> longList = compound.getLongList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            longList.remove(number.longValue());
                    if (longList.isEmpty()) compound.removeKey(tag);
                }
            }
            case NBTTagFloatList -> {
                if (singleObject instanceof Number) {
                    NBTList<Float> floatList = compound.getFloatList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            floatList.remove(number.floatValue());
                    if (floatList.isEmpty()) compound.removeKey(tag);
                }
            }
            case NBTTagDoubleList -> {
                if (singleObject instanceof Number) {
                    NBTList<Double> doubleList = compound.getDoubleList(tag);
                    for (Object o : object)
                        if (o instanceof Number number)
                            doubleList.remove(number.doubleValue());
                    if (doubleList.isEmpty()) compound.removeKey(tag);
                }
            }
            case NBTTagStringList -> {
                if (singleObject instanceof String) {
                    NBTList<String> stringList = compound.getStringList(tag);
                    for (Object o : object)
                        if (o instanceof String string)
                            stringList.remove(string);
                    if (stringList.isEmpty()) compound.removeKey(tag);
                }
            }
            case NBTTagCompoundList -> {
                // Not sure if possible, leave for now
                // Error has been added in ExprTagOfNBT
            }
        }
    }

    @Nullable
    private static Object resolveFromList(NBTCompound compound, String tag, NBTCustomType type) {
        Class<?> typeClass = type.getTypeClass();
        String tagWithoutBracket = tag.split("\\[")[0];
        if (!typeClass.isArray() && compound.hasTag(tagWithoutBracket)) {
            try {
                if (type == NBTCustomType.NBTTagCompound) {
                    return compound.resolveCompound(tag);
                }
                return compound.resolveOrNull(tag, typeClass);
            } catch (NbtApiException ignore) {
                // Errors if the list is the wrong type
            }
        }
        return null;
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
    @SuppressWarnings("DataFlowIssue")
    public static @Nullable Object getTag(@NotNull String tag, @NotNull NBTCompound compound, @NotNull NBTCustomType type) {
        Pair<String, NBTCompound> nestedCompound = getNestedCompound(tag, compound, type != NBTCustomType.NBTTagCompound);
        if (nestedCompound == null) return null;

        tag = nestedCompound.first();
        compound = nestedCompound.second();

        // If the tag has [number] we grab from the list/array
        if (tag.contains("[") && tag.contains("]")) {
            return resolveFromList(compound, tag, type);
        }

        // If the tag is empty/wrongtype, return null, unless it's a compound then we create an empty compound
        if (type != NBTCustomType.NBTTagCompound && (!compound.hasTag(tag) || compound.getType(tag) != type.getNbtType()))
            return null;
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
                        return uuid;
                    }
                } catch (NbtApiException ignore) {
                }
            }
            case NBTTagBoolean -> {
                return compound.getBoolean(tag);
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
                if (SkBee.isDebug())
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
        } else {
            NBTCustomBlock nbtCustomBlock = new NBTCustomBlock(block);
            nbtCustomBlock.mergeCompound(compound);
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
