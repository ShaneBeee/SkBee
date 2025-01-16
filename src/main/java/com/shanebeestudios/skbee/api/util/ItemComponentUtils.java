package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.slot.Slot;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.inventory.ItemStack;
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

}
