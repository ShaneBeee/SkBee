package com.shanebeestudios.skbee.api.NBT;

import ch.njol.util.StringUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public enum NBTCustomType {

    NBTTagEnd("tag end", NBTType.NBTTagEnd),
    NBTTagByte("byte", NBTType.NBTTagByte),
    NBTTagShort("short", NBTType.NBTTagShort),
    NBTTagInt("int", NBTType.NBTTagInt),
    NBTTagLong("long", NBTType.NBTTagLong),
    NBTTagFloat("float", NBTType.NBTTagFloat),
    NBTTagDouble("double", NBTType.NBTTagDouble),
    NBTTagByteArray("byte array", NBTType.NBTTagByteArray),
    NBTTagIntArray("int array", NBTType.NBTTagIntArray),
    NBTTagString("string", NBTType.NBTTagString),
    NBTTagCompound("compound", NBTType.NBTTagCompound),
    NBTTagDoubleList("double list", NBTType.NBTTagList),
    NBTTagFloatList("float list", NBTType.NBTTagList),
    NBTTagLongList("long list", NBTType.NBTTagList),
    NBTTagIntList("int list", NBTType.NBTTagList),
    NBTTagCompoundList("compound list", NBTType.NBTTagList),
    NBTTagStringList("string list", NBTType.NBTTagList);

    String name;
    NBTType nbtType;

    NBTCustomType(String name, NBTType nbtType) {
        this.name = name + " tag";
        this.nbtType = nbtType;
    }

    public String getName() {
        return name;
    }

    private static final Map<String,NBTCustomType> BY_NAME = new HashMap<>();
    private static final Map<NBTType,NBTCustomType> BY_TYPE = new HashMap<>();

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
