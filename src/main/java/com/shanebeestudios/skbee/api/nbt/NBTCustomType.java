package com.shanebeestudios.skbee.api.nbt;

import ch.njol.util.StringUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum NBTCustomType {

    NBTTagEnd("tag end", NBTType.NBTTagEnd),
    // Numbers
    NBTTagByte("byte", NBTType.NBTTagByte, Byte.class),
    NBTTagShort("short", NBTType.NBTTagShort, Short.class),
    NBTTagInt("int", NBTType.NBTTagInt, Integer.class),
    NBTTagLong("long", NBTType.NBTTagLong, Long.class),
    NBTTagFloat("float", NBTType.NBTTagFloat, Float.class),
    NBTTagDouble("double", NBTType.NBTTagDouble, Double.class),
    // Other
    NBTTagString("string", NBTType.NBTTagString, String.class),
    NBTTagCompound("compound", NBTType.NBTTagCompound, NBTCompound.class),
    // Custom
    NBTTagUUID("uuid", NBTType.NBTTagIntArray, String.class),
    NBTTagBoolean("boolean", NBTType.NBTTagByte, Boolean.class),
    // Lists and Arrays
    NBTTagByteArray("byte array", NBTType.NBTTagByteArray, Number[].class),
    NBTTagIntArray("int array", NBTType.NBTTagIntArray, Number[].class),
    NBTTagDoubleList("double list", NBTType.NBTTagList, Number[].class),
    NBTTagFloatList("float list", NBTType.NBTTagList, Number[].class),
    NBTTagLongList("long list", NBTType.NBTTagList, Number[].class),
    NBTTagIntList("int list", NBTType.NBTTagList, Number[].class),
    NBTTagCompoundList("compound list", NBTType.NBTTagList, NBTCompound[].class),
    NBTTagStringList("string list", NBTType.NBTTagList, String[].class);

    final String name;
    final NBTType nbtType;
    final Class<?> typeClass;

    NBTCustomType(String name, NBTType nbtType) {
        this(name, nbtType, Void.class);
    }

    NBTCustomType(String name, NBTType nbtType, Class<?> typeClass) {
        this.name = name + " tag";
        this.nbtType = nbtType;
        this.typeClass = typeClass;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getTypeClass() {
        return this.typeClass;
    }

    public NBTType getNbtType() {
        return this.nbtType;
    }

    private static final Map<String, NBTCustomType> BY_NAME = new HashMap<>();
    private static final Map<NBTType, NBTCustomType> BY_TYPE = new HashMap<>();

    static {
        for (NBTCustomType type : NBTCustomType.values()) {
            if (type != NBTTagEnd)
                BY_NAME.put(type.name, type);
            BY_TYPE.put(type.nbtType, type);
        }
    }

    @Nullable
    public static NBTCustomType fromName(String name) {
        String s = name.toLowerCase(Locale.ROOT);
        if (BY_NAME.containsKey(s)) {
            return BY_NAME.get(s);
        }
        return null;
    }

    @Nullable
    public static NBTCustomType getByTag(NBTCompound compound, String key) {
        if (compound == null) return null;
        NBTType nbtType = compound.getType(key);
        if (BY_TYPE.containsKey(nbtType)) {
            if (nbtType == NBTType.NBTTagList) {
                if (!compound.getIntegerList(key).isEmpty())
                    return NBTTagIntList;
                else if (!compound.getLongList(key).isEmpty())
                    return NBTTagLongList;
                else if (!compound.getFloatList(key).isEmpty())
                    return NBTTagFloatList;
                else if (!compound.getDoubleList(key).isEmpty())
                    return NBTTagDoubleList;
                else if (!compound.getCompoundList(key).isEmpty())
                    return NBTTagCompoundList;
                else if (!compound.getStringList(key).isEmpty())
                    return NBTTagStringList;
            }
            return BY_TYPE.get(nbtType);
        }
        return null;
    }

    public static String getNames() {
        List<String> names = new ArrayList<>(BY_NAME.keySet());
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
