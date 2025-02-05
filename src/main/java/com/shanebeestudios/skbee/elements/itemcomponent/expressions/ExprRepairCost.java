package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.Math2;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - repair cost component")
@Description({"The number of experience levels to add to the base level cost when repairing, combining, or renaming this item with an anvil.",
    "Must be a non-negative integer, defaults to 0."})
@Examples({"set repair cost component of player's tool to 3",
    "add 2 to repair cost component of player's tool",
    "subtract 1 from repair cost component of player's tool",
    "reset repair cost component of player's tool",
    "delete repair cost component of {_item}",
    "if repair cost component of player's tool > 0:"})
@Since("3.6.0")
public class ExprRepairCost extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprRepairCost.class, Number.class, "repair cost component", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable Number convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.REPAIR_COST)) {
            return itemStack.getData(DataComponentTypes.REPAIR_COST);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.REMOVE || mode == ChangeMode.ADD)
            return CollectionUtils.array(Number.class);
        else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int cost = delta != null && delta[0] instanceof Number num ? num.intValue() : 0;
        ItemUtils.modifyItems(getExpr().getArray(event), itemStack -> {
            if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.REPAIR_COST);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.REPAIR_COST);
            } else {
                int changeValue = cost;
                if (mode == ChangeMode.ADD && itemStack.hasData(DataComponentTypes.REPAIR_COST)) {
                    changeValue += itemStack.getData(DataComponentTypes.REPAIR_COST);
                } else if (mode == ChangeMode.REMOVE && itemStack.hasData(DataComponentTypes.REPAIR_COST)) {
                    changeValue = itemStack.getData(DataComponentTypes.REPAIR_COST) - changeValue;
                }
                itemStack.setData(DataComponentTypes.REPAIR_COST, Math2.fit(0, changeValue, Integer.MAX_VALUE));
            }
        });
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "repair cost component";
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
