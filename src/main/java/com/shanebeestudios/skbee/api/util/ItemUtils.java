package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.Classes;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    /**
     * Add ItemTypes to a list of ItemStacks
     * <br>This will split up oversized stacks as well
     *
     * @param itemTypes  List of ItemTypes to add
     * @param itemStacks Option list of ItemStacks to modify
     * @return List of ItemStacks split up correctly
     */
    public static List<ItemStack> addItemTypesToList(List<ItemType> itemTypes, List<ItemStack> itemStacks) {
        List<ItemStack> originalList = itemStacks != null ? new ArrayList<>(itemStacks) : new ArrayList<>();
        ItemStack[] buffer = originalList.toArray(new ItemStack[1000]);
        for (ItemType itemType : itemTypes) {
            // Split up oversized stacks
            itemType.addTo(buffer);
        }
        List<ItemStack> newList = new ArrayList<>();
        for (ItemStack itemStack : buffer) {
            if (itemStack != null && !itemStack.isEmpty()) newList.add(itemStack);
        }
        return newList;
    }

    /**
     * Remove list of ItemTypes from a list of ItemStacks
     * <br>This will split up oversized stacks as well
     *
     * @param itemStacks List of ItemStacks to have ItemTypes removed from
     * @param itemTypes  List of ItemTypes to remove
     * @return Updated list of ItemStacks
     */
    public static List<ItemStack> removeItemTypesFromList(List<ItemStack> itemStacks, List<ItemType> itemTypes) {
        List<ItemStack> copyList = new ArrayList<>(itemStacks);
        for (ItemType itemType : itemTypes) {
            itemType.removeFrom(copyList);
        }
        List<ItemStack> newItems = new ArrayList<>();
        for (ItemStack itemStack : copyList) {
            if (itemStack != null && !itemStack.isEmpty()) newItems.add(itemStack);
        }
        return newItems;
    }

}
