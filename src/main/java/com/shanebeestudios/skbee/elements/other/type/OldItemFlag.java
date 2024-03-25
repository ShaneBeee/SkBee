package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
// TODO temp class to hold old flags
public class OldItemFlag {

    private static final Map<ItemFlag, OldItemFlag> FLAGS = new HashMap<>();
    private static final Map<String, OldItemFlag> BY_NAME = new HashMap<>();

    static {
        register(ItemFlag.HIDE_ENCHANTS, "enchants_flag");
        register(ItemFlag.HIDE_ATTRIBUTES, "attributes_flag");
        register(ItemFlag.HIDE_UNBREAKABLE, "unbreakable_flag");
        register(ItemFlag.HIDE_DESTROYS, "destroys_flag");
        register(ItemFlag.HIDE_PLACED_ON, "place_on_flag");
        register(ItemFlag.HIDE_POTION_EFFECTS, "potion_effects_flag");
        register(ItemFlag.HIDE_DYE, "dye_flag");
        if (Skript.fieldExists(ItemFlag.class, "HIDE_ARMOR_TRIM")) {
            register(ItemFlag.HIDE_ARMOR_TRIM, "armor_trim_flag");
        }
    }

    private static void register(ItemFlag bukkitFlag, String name) {
        OldItemFlag oldItemFlag = new OldItemFlag(bukkitFlag, name);
        FLAGS.put(bukkitFlag, oldItemFlag);
        BY_NAME.put(name, oldItemFlag);
    }

    public static OldItemFlag getFromBukkit(ItemFlag bukkitItemFlag) {
        return FLAGS.get(bukkitItemFlag);
    }

    private final ItemFlag bukkitItemFlag;
    private final String name;

    OldItemFlag(ItemFlag bukkitFlag, String name) {
        this.bukkitItemFlag = bukkitFlag;
        this.name = name;
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
