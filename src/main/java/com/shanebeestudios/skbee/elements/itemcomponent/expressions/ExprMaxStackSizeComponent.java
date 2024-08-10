package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Max Stack Size")
@Description({"Represents the max stack size of an item (Requires Minecraft 1.20.5+).",
    "**Changers**:",
    "- `set` = Set the max stack size, must be an integer between 1 and 99.",
    "- `reset` = Resets back to default stack size."})
@Examples({"set max stack size component of player's tool to 1",
    "reset max stack size component of player's tool"})
@Since("3.6.0")
public class ExprMaxStackSizeComponent extends SimplePropertyExpression<ItemType, Number> {

    static {
        if (Skript.methodExists(ItemMeta.class, "setMaxStackSize", Integer.class)) {
            register(ExprMaxStackSizeComponent.class, Number.class, "max stack size component", "itemtypes");
        }
    }

    @Override
    public @Nullable Number convert(ItemType itemType) {
        ItemMeta itemMeta = itemType.getItemMeta();
        if (!itemMeta.hasMaxStackSize()) return itemType.getMaterial().getMaxStackSize();
        return itemMeta.getMaxStackSize();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.RESET) return CollectionUtils.array();
        else if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Integer maxStackSize = delta != null && delta[0] instanceof Number num ? MathUtil.clamp(num.intValue(), 1, 99) : null;
        for (ItemType itemType : getExpr().getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            itemMeta.setMaxStackSize(maxStackSize);
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "max stack size component";
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
