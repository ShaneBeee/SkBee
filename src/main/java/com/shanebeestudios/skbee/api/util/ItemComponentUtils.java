package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.util.slot.Slot;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class for {@link DataComponentType DataComponents}
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentUtils {

    /**
     * Quick method to modify {@link DataComponentType DataComponents} of items.
     *
     * @param objects Item based objects to modify (Accepts can be {@link ItemStack}, {@link ItemType} or {@link Slot})
     * @param mode    ChangeMode passed from expression
     * @param type    DataComponentType to modify
     * @param value   Value to set
     * @param <T>     DataComponentType value
     */
    public static <T> void modifyComponent(Object[] objects, ChangeMode mode, @NotNull DataComponentType.Valued<T> type, @Nullable T value) {
        ItemUtils.modifyItems(objects, itemStack -> {
            if (mode == ChangeMode.SET && value != null) {
                itemStack.setData(type, value);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(type);
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(type);
            }
        });
    }

    /**
     * Quick method to modify {@link DataComponentType DataComponents} of items.
     *
     * @param objects  Item based objects to modify (Accepts can be {@link ItemStack}, {@link ItemType} or {@link Slot})
     * @param type     DataComponentType to modify
     * @param consumer BiConsumer to accept the DataComponentType value and ItemStack
     * @param <T>      DataComponentType value
     */
    public static <T> void modifyComponent(Object[] objects, @NotNull DataComponentType.Valued<T> type, BiConsumer<T, ItemStack> consumer) {
        ItemUtils.modifyItems(objects, itemStack -> consumer.accept(itemStack.getData(type), itemStack));
    }

    /**
     * Quick method to take a peak at {@link DataComponentType DataComponents} of items.
     *
     * @param objects  Item based objects to modify (Accepts can be {@link ItemStack}, {@link ItemType} or {@link Slot})
     * @param type     DataComponentType to modify
     * @param consumer Consumer to accept the DataComponentType value
     * @param <T>      DataComponentType value
     */
    public static <T> void takeAPeakAtComponent(Object[] objects, @NotNull DataComponentType.Valued<T> type, Consumer<T> consumer) {
        ItemUtils.modifyItems(objects, itemStack -> consumer.accept(itemStack.getData(type)));
    }

}
