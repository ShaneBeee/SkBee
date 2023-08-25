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
    NBTTagByte("byte", NBTType.NBTTagByte, Number.class),
    NBTTagShort("short", NBTType.NBTTagShort, Number.class),
    NBTTagInt("int", NBTType.NBTTagInt, Number.class),
    NBTTagLong("long", NBTType.NBTTagLong, Number.class),
    NBTTagFloat("float", NBTType.NBTTagFloat, Number.class),
    NBTTagDouble("double", NBTType.NBTTagDouble, Number.class),
    NBTTagString("string", NBTType.NBTTagString, String.class),
    NBTTagUUID("uuid", NBTType.NBTTagIntArray, String.class),
    NBTTagCompound("compound", NBTType.NBTTagCompound, NBTCompound.class),
    NBTTagByteArray("byte array", NBTType.NBTTagByteArray, Number[].class, true),
    NBTTagIntArray("int array", NBTType.NBTTagIntArray, Number[].class, true),
    NBTTagDoubleList("double list", NBTType.NBTTagList, Number[].class, true),
    NBTTagFloatList("float list", NBTType.NBTTagList, Number[].class, true),
    NBTTagLongList("long list", NBTType.NBTTagList, Number[].class, true),
    NBTTagIntList("int list", NBTType.NBTTagList, Number[].class, true),
    NBTTagCompoundList("compound list", NBTType.NBTTagList, NBTCompound[].class, true),
    NBTTagStringList("string list", NBTType.NBTTagList, String[].class,true);

    final String name;
    final NBTType nbtType;
    final Class<?> typeClass;
    final boolean isList;

    NBTCustomType(String name, NBTType nbtType) {
        this(name, nbtType, Void.class);
    }

    NBTCustomType(String name, NBTType nbtType, Class<?> typeClass) {
        this(name, nbtType, typeClass, false);
    }

    NBTCustomType(String name, NBTType nbtType, Class<?> typeClass, boolean isList) {
        this.name = name + " tag";
        this.nbtType = nbtType;
        this.typeClass = typeClass;
        this.isList = isList;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getTypeClass() {
        return this.typeClass;
    }

    public boolean isList() {
        return this.isList;
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
                if (compound.getIntegerList(key).size() > 0)
                    return NBTTagIntList;
                else if (compound.getLongList(key).size() > 0)
                    return NBTTagLongList;
                else if (compound.getFloatList(key).size() > 0)
                    return NBTTagFloatList;
                else if (compound.getDoubleList(key).size() > 0)
                    return NBTTagDoubleList;
                else if (compound.getCompoundList(key).size() > 0)
                    return NBTTagCompoundList;
                else if (compound.getStringList(key).size() > 0)
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
