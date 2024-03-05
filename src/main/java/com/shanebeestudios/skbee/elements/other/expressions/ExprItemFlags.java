package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Name("ItemFlag - ItemFlags of Items")
@Description("Get/Set the ItemFlags of an item.")
@Examples({"set {_flags::*} to item flags of player's tool",
        "add hide enchants to item flags of player's tool",
        "add hide attributes to item flags of player's tool",
        "add hide enchants and hide attributes to item flags of player's tool",
        "remove hide enchants from item flags of player's tool",
        "remove hide attributes from item flags of player's tool",
        "delete item flags of player's tool",
        "reset item flags of player's tool"})
@Since("3.4.0")
public class ExprItemFlags extends PropertyExpression<ItemType, ItemFlag> {

    static {
        register(ExprItemFlags.class, ItemFlag.class, "item[ ]flags", "itemtypes");
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ItemFlag @NotNull [] get(Event event, ItemType[] source) {
        List<ItemFlag> itemFlags = new ArrayList<>();
        for (ItemType itemType : source) {
            itemFlags.addAll(itemType.getItemMeta().getItemFlags());
        }
        return itemFlags.toArray(new ItemFlag[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(ItemFlag[].class);
        } else if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            for (ItemType itemType : getExpr().getArray(event)) {
                ItemMeta itemMeta = itemType.getItemMeta();
                Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
                itemMeta.removeItemFlags(itemFlags.toArray(new ItemFlag[0]));
                itemType.setItemMeta(itemMeta);
            }
        } else if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            if (delta != null && delta instanceof ItemFlag[] itemFlags) {
                for (ItemType itemType : getExpr().getArray(event)) {
                    ItemMeta itemMeta = itemType.getItemMeta();
                    if (mode == Changer.ChangeMode.ADD) {
                        itemMeta.addItemFlags(itemFlags);
                    } else {
                        itemMeta.removeItemFlags(itemFlags);
                    }
                    itemType.setItemMeta(itemMeta);
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ItemFlag> getReturnType() {
        return ItemFlag.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item flags of " + getExpr().toString(e, d);
    }

}
