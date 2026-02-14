package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ExprArmorTrimItem extends SimplePropertyExpression<ItemType, ArmorTrim> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprArmorTrimItem.class, ArmorTrim.class,
                "armor trim", "itemtypes")
            .name("ArmorTrim - Item")
            .description("Represents the armor trim of an item. You can get, set, add or delete/reset.",
                "Reset will reset the trim back to any default vanilla value.",
                "Delete will remove any trim on the item.")
            .examples("add armor trim from gold_material and eye_pattern to armor trim of player's leggings",
                "set armor trim of player's helmet to armor trim from gold_material and eye_pattern",
                "delete armor trim of player's leggings",
                "reset armor trim of player's boots")
            .since("2.13.0")
            .register();
    }

    @Override
    public @Nullable ArmorTrim convert(ItemType itemType) {
        ItemStack itemStack = itemType.getRandom();
        if (itemStack != null && itemStack.hasData(DataComponentTypes.TRIM)) {
            ItemArmorTrim data = itemStack.getData(DataComponentTypes.TRIM);
            if (data != null) return data.armorTrim();
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD) return CollectionUtils.array(ArmorTrim.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ArmorTrim armorTrim;
        if (delta != null && delta[0] instanceof ArmorTrim trim) {
            armorTrim = trim;
        } else {
            armorTrim = null;
        }

        ItemUtils.modifyItems(getExpr().getArray(event), itemStack -> {
            if (armorTrim != null) {
                itemStack.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(armorTrim).build());
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.TRIM);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.TRIM);
            }
        });
    }

    @Override
    public @NotNull Class<? extends ArmorTrim> getReturnType() {
        return ArmorTrim.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "armor trim";
    }

}
