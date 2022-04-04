package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.reflection.McReflection;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * Utility class to handle text components
 */
public class TextUtils {

    private static final NBTApi api = SkBee.getPlugin().getNbtApi();
    private static final boolean HAS_TRANSLATION = Skript.classExists("net.kyori.adventure.translation.Translatable");

    public static String getTranslation(Object object) {
        if (object instanceof ItemType) {
            return translateItemType(((ItemType) object));
        } else if (object instanceof Slot) {
            ItemStack item = ((Slot) object).getItem();
            if (HAS_TRANSLATION && item != null) {
                return ((Translatable) item).translationKey();
            }
        } else if (HAS_TRANSLATION && object instanceof Translatable) {
            return ((Translatable) object).translationKey();
        } else if (HAS_TRANSLATION && object instanceof Entity) {
            return ((Entity) object).getType().translationKey();
        } else if (object instanceof String) {
            return ((String) object);
        }
        return null;
    }

    public static String translateItemType(ItemType itemType) {
        ItemStack itemStack = itemType.getRandom();
        assert itemStack != null;
        String trans = McReflection.getTranslateKey(itemStack);
        if (trans != null) {
            return trans;
        }
        Material material = itemStack.getType();
        String type = material.isBlock() ? "block" : "item";
        String raw = itemType.getRawNames().get(0).replace("minecraft:", "");
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta) {
            StringBuilder builder = new StringBuilder("item.minecraft.");
            String nbt = api.getNBT(itemType, NBTApi.ObjectType.ITEM_TYPE);
            if (nbt != null) {
                String pot = api.getTag("Potion", new NBTContainer(nbt)).toString();
                if (pot != null) {
                    if (material == Material.POTION) {
                        builder.append("potion");
                    } else if (material == Material.SPLASH_POTION) {
                        builder.append("splash_potion");
                    } else if (material == Material.LINGERING_POTION) {
                        builder.append("lingering_potion");
                    } else if (material == Material.TIPPED_ARROW) {
                        builder.append("tipped_arrow");
                    }
                    builder.append(".effect.").append(pot.replace("minecraft:", ""));
                    return builder.toString();
                }
            }
        }
        return type + ".minecraft." + raw;
    }

}
