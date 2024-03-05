package com.shanebeestudios.skbee.elements.other.type;

import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Deprecated
// TODO temp class to hold old flags
public class OldItemFlag {

    private static final Map<ItemFlag, OldItemFlag> FLAGS = new HashMap<>();
    private static final Map<String, OldItemFlag> BY_NAME = new HashMap<>();

    public static OldItemFlag ENCHANTS_FLAG = new OldItemFlag(ItemFlag.HIDE_ENCHANTS, "enchants_flag");
    public static OldItemFlag ATTRIBUTES_FLAG = new OldItemFlag(ItemFlag.HIDE_ATTRIBUTES, "attributes_flag");
    public static OldItemFlag UNBREAKABLE_FLAG = new OldItemFlag(ItemFlag.HIDE_UNBREAKABLE, "unbreakable_flag");
    public static OldItemFlag DESTROYS_FLAG = new OldItemFlag(ItemFlag.HIDE_DESTROYS, "destroys_flag");
    public static OldItemFlag PLACED_ON_FLAG = new OldItemFlag(ItemFlag.HIDE_PLACED_ON, "place_on_flag");
    public static OldItemFlag POTION_EFFECTS_FLAG = new OldItemFlag(ItemFlag.HIDE_POTION_EFFECTS, "potion_effects_flag");
    public static OldItemFlag DYE_FLAG = new OldItemFlag(ItemFlag.HIDE_DYE, "dye_flag");
    public static OldItemFlag ARMOR_TRIM_FLAG = new OldItemFlag(ItemFlag.HIDE_ARMOR_TRIM, "armor_trim_flag");

    public static OldItemFlag getFromBukkit(ItemFlag bukkitItemFlag) {
        return FLAGS.get(bukkitItemFlag);
    }

    private final ItemFlag bukkitItemFlag;
    private final String name;

    OldItemFlag(ItemFlag bukkitFlag, String name) {
        this.bukkitItemFlag = bukkitFlag;
        this.name = name;
        FLAGS.put(bukkitFlag, this);
        BY_NAME.put(name, this);
    }

    public ItemFlag getBukkitItemFlag() {
        return this.bukkitItemFlag;
    }

    public String getName() {
        return this.name;
    }

    public static OldItemFlag[] values() {
        return FLAGS.values().toArray(new OldItemFlag[0]);
    }

    @Nullable
    public static OldItemFlag parse(String string) {
        String s = string.toLowerCase(Locale.ROOT).replace(" ", "_");
        return BY_NAME.get(s);
    }

}
