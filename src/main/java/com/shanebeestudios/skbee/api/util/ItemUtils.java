package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    private static final boolean HAS_KEY = Skript.methodExists(AttributeModifier.class, "getKey");

    /**
     * Check if an ItemMeta already contains a specific AttributeModifier
     *
     * @param itemMeta ItemMeta to check for modifier
     * @param modifier Modifier to compare
     * @return True if modifier already exists on ItemMeta else false
     */
    @SuppressWarnings("DataFlowIssue")
    public static boolean hasAttributeModifier(ItemMeta itemMeta, AttributeModifier modifier) {
        if (itemMeta.hasAttributeModifiers()) {
            for (AttributeModifier mod : itemMeta.getAttributeModifiers().values()) {
                if (HAS_KEY && modifier.getKey().equals(mod.getKey())) {
                    // If the same key already exists, we exit
                    return true;
                } else if (modifier.getName().equalsIgnoreCase(mod.getName())) {
                    // If the same name already exists, we exit
                    return false;
                }
            }
        }
        return false;
    }

}
