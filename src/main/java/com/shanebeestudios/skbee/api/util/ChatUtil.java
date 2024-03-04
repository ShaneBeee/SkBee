package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.slot.Slot;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.Translatable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for handing colors
 */
@SuppressWarnings("deprecation")
public enum ChatUtil {

    BLACK(SkriptColor.BLACK, ChatColor.BLACK),
    DARK_GRAY(SkriptColor.DARK_GREY, ChatColor.DARK_GRAY),
    GRAY(SkriptColor.LIGHT_GREY, ChatColor.GRAY),
    WHITE(SkriptColor.WHITE, ChatColor.WHITE),
    DARK_BLUE(SkriptColor.DARK_BLUE, ChatColor.DARK_BLUE),
    BLUE(SkriptColor.BROWN, ChatColor.BLUE),
    DARK_AQUA(SkriptColor.DARK_CYAN, ChatColor.DARK_AQUA),
    AQUA(SkriptColor.LIGHT_CYAN, ChatColor.AQUA),
    DARK_GREEN(SkriptColor.DARK_GREEN, ChatColor.DARK_GREEN),
    GREEN(SkriptColor.LIGHT_GREEN, ChatColor.GREEN),
    YELLOW(SkriptColor.YELLOW, ChatColor.YELLOW),
    GOLD(SkriptColor.ORANGE, ChatColor.GOLD),
    DARK_RED(SkriptColor.DARK_RED, ChatColor.DARK_RED),
    RED(SkriptColor.LIGHT_RED, ChatColor.RED),
    DARK_PURPLE(SkriptColor.DARK_PURPLE, ChatColor.DARK_PURPLE),
    LIGHT_PURPLE(SkriptColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE);

    private final SkriptColor skriptColor;
    private final ChatColor bungeeChatColor;

    ChatUtil(SkriptColor skript, ChatColor bungee) {
        this.skriptColor = skript;
        this.bungeeChatColor = bungee;
    }

    public static SkriptColor getSkriptColorByBungee(ChatColor chatColor) {
        for (ChatUtil value : ChatUtil.values()) {
            if (chatColor == value.bungeeChatColor) {
                return value.skriptColor;
            }
        }
        return null;
    }

    public static TextColor getTextColorFromColor(Color color) {
        return TextColor.color(color.asBukkitColor().asRGB());
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static SkriptColor getSkriptColorFromTextColor(TextColor textColor) {
        int intValue = textColor.value();
        return SkriptColor.fromBukkitColor(org.bukkit.Color.fromRGB(intValue));
    }

    public static @Nullable String getTranslation(Object object) {
        if (object instanceof Entity entity) {
            EntityType type = entity.getType();
            return Bukkit.getUnsafe().getTranslationKey(type);
        } else if (object instanceof EntityData<?> entityData) {
            EntityType type = EntityUtils.toBukkitEntityType(entityData);
            return Bukkit.getUnsafe().getTranslationKey(type);
        } else if (object instanceof ItemStack itemStack) {
            return itemStack.translationKey();
        } else if (object instanceof ItemType itemType) {
            ItemStack itemStack = itemType.getRandom();
            return itemStack.translationKey();
        } else if (object instanceof Slot slot) {
            ItemStack itemStack = slot.getItem();
            return getTranslation(itemStack);
        } else if (object instanceof String string) {
            return string;
        } else if (object instanceof Translatable translatable) {
            return translatable.translationKey();
        }
        return null;
    }

}
