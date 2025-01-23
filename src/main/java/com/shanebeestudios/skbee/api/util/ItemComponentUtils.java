package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.slot.Slot;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for {@link DataComponentType DataComponents}
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentUtils {

    /**
     * Quick method to modify {@link DataComponentType DataComponents} of items
     *
     * @param objects Item based objects to modify (Accepts can be {@link ItemStack}, {@link ItemType} or {@link Slot})
     * @param mode    ChangeMode passed from expression
     * @param type    DataComponentType to modify
     * @param value   Value to set
     * @param <T>     DataComponentType value
     */
    public static <T> void modifyComponent(Object[] objects, Changer.ChangeMode mode, @NotNull DataComponentType.Valued<T> type, @Nullable T value) {
        ItemUtils.modifyItems(objects, itemStack -> {
            if (mode == Changer.ChangeMode.SET && value != null) {
                itemStack.setData(type, value);
            } else if (mode == Changer.ChangeMode.DELETE) {
                itemStack.unsetData(type);
            } else if (mode == Changer.ChangeMode.RESET) {
                itemStack.resetData(type);
            }
        });
    }

    public static PotionType getPotionType(ItemStack itemStack) {
        if (itemStack.hasData(DataComponentTypes.POTION_CONTENTS)) {
            PotionContents data = itemStack.getData(DataComponentTypes.POTION_CONTENTS);
            if (data != null) return data.potion();
        }
        return null;
    }

    public static void setPotionType(ItemStack itemStack, @Nullable PotionType potionType) {
        if (potionType != null) {
            PotionContents potionContents = PotionContents.potionContents().potion(potionType).build();
            itemStack.setData(DataComponentTypes.POTION_CONTENTS, potionContents);
        } else {
            itemStack.unsetData(DataComponentTypes.POTION_CONTENTS);
        }
    }

    /**
     * Get the ItemName/CustomName of an ItemStack
     *
     * @param itemStack ItemStack to get name from
     * @param itemName  Whether item or custom name
     * @return Name from item
     */
    public static Component getItemName(ItemStack itemStack, boolean itemName) {
        DataComponentType.Valued<Component> type = itemName ? DataComponentTypes.ITEM_NAME : DataComponentTypes.CUSTOM_NAME;
        if (itemStack.hasData(type)) {
            return itemStack.getData(type);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemName ? itemMeta.itemName() : itemMeta.displayName();
    }

    /**
     * Set the ItemName/CustomName of an ItemStack
     * <p>Null name value will reset to vanilla value</p>
     *
     * @param itemStack ItemStack to modify name
     * @param name      Name to modify
     * @param itemName  Whether item or custom name
     */
    public static void setItemName(ItemStack itemStack, @Nullable Component name, boolean itemName) {
        DataComponentType.Valued<Component> type = itemName ? DataComponentTypes.ITEM_NAME : DataComponentTypes.CUSTOM_NAME;
        if (name == null) {
            itemStack.resetData(type);
        } else {
            itemStack.setData(type, name);
        }
    }

}
