package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.Classes;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class ItemUtils {

    /**
     * If AttributeModifier class is using NamespacedKeys
     */
    public static final boolean HAS_KEY = Skript.methodExists(AttributeModifier.class, "getKey");
    /**
     * If AttributeModifier class is using EquipmentSlotGroup
     */
    public static final boolean HAS_EQUIPMENT_SLOT_GROUP = Skript.methodExists(AttributeModifier.class, "getSlotGroup");

    /**
     * Check if an ItemMeta already contains a specific AttributeModifier
     *
     * @param itemMeta  ItemMeta to check for modifier
     * @param attribute Attribute to compare
     * @param modifier  Modifier to compare
     * @return True if modifier already exists on ItemMeta else false
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean hasAttributeModifier(ItemMeta itemMeta, Attribute attribute, AttributeModifier modifier) {
        if (itemMeta.hasAttributeModifiers()) {
            Collection<AttributeModifier> attributeModifiers = itemMeta.getAttributeModifiers(attribute);
            if (attributeModifiers == null) return false;

            for (AttributeModifier mod : attributeModifiers) {
                if (HAS_KEY && modifier.getKey().equals(mod.getKey())) {
                    // If the same key already exists, we exit
                    return true;
                } else if (modifier.getName().equalsIgnoreCase(mod.getName())) {
                    // If the same name already exists, we exit
                    return true;
                }
            }

        }
        return false;
    }

    @SuppressWarnings({"deprecation", "removal"})
    public static String attributeModifierToString(AttributeModifier attributeModifier) {
        StringBuilder builder = new StringBuilder("AttributeModifier{");
        if (HAS_KEY) {
            builder.append("key='").append(attributeModifier.getKey()).append("'");
        } else {
            builder.append("name='").append(attributeModifier.getName()).append("'");
            builder.append(",uuid='").append(attributeModifier.getUniqueId()).append("'");
        }
        builder.append(",amount=").append(attributeModifier.getAmount());
        if (HAS_EQUIPMENT_SLOT_GROUP) {
            builder.append(",slot=").append(Classes.toString(attributeModifier.getSlotGroup()));
        } else {
            builder.append(",slot=").append(Classes.toString(attributeModifier.getSlot()));
        }
        builder.append(",operation=").append(Classes.toString(attributeModifier.getOperation()));
        builder.append("}");
        return builder.toString();
    }

}
