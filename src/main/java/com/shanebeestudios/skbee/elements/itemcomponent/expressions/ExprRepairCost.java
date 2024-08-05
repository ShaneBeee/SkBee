package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Repair Cost")
@Description({"The number of experience levels to add to the base level cost when repairing, combining, or renaming this item with an anvil.",
    "Must be a non-negative integer, defaults to 0."})
@Examples({"set repair cost of player's tool to 3",
    "if repair cost of player's tool > 0:"})
@Since("INSERT VERSION")
public class ExprRepairCost extends SimplePropertyExpression<ItemType, Number> {

    static {
        register(ExprRepairCost.class, Number.class, "repair cost", "itemtypes");
    }

    @Override
    public @Nullable Number convert(ItemType itemType) {
        ItemMeta itemMeta = itemType.getItemMeta();
        if (itemMeta instanceof Repairable repairable) {
            return repairable.getRepairCost();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int cost = delta != null && delta[0] instanceof Number num ? Math.max(num.intValue(), 0) : 0;
        for (ItemType itemType : getExpr().getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            if (itemMeta instanceof Repairable repairable) {
                repairable.setRepairCost(cost);
                itemType.setItemMeta(itemMeta);
            }
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "repair cost";
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
