package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Max Stack Size")
@Description({"Represents the max stack size of an item.",
    "See [**MaxStackSize**](https://minecraft.wiki/w/Data_component_format#max_stack_size) on McWiki for more details.",
    "Requires Paper 1.21.3+",
    "",
    "**Changers**:",
    "- `set` = Set the max stack size, must be an integer between 1 and 99.",
    "- `delete` = Will remove the component from the item.",
    "- `reset` = Resets back to default stack size."})
@Examples({"set max stack size component of player's tool to 1",
    "reset max stack size component of player's tool"})
@Since("3.6.0")
public class ExprMaxStackSizeComponent extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprMaxStackSizeComponent.class, Number.class, "max stack size component", "itemtypes");
    }

    @Override
    public @Nullable Number convert(Object itemType) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(itemType);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.MAX_STACK_SIZE)) {
            return itemStack.getData(DataComponentTypes.MAX_STACK_SIZE);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array();
        else if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Integer maxStackSize = delta != null && delta[0] instanceof Number num ? MathUtil.clamp(num.intValue(), 1, 99) : null;
        ItemUtils.modifyItems(getExpr().getArray(event), itemStack -> {
            if (mode == ChangeMode.SET && maxStackSize != null) {
                itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.MAX_STACK_SIZE);
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.MAX_STACK_SIZE);
            }
        });
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
