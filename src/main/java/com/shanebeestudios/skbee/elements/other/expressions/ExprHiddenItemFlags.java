package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
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

@Name("Hidden Item Flags")
@Description("Represents the hidden item flags of an item. Can be added to, remove from or cleared/reset.")
@Examples({"add enchants flag to hidden item flags of player's tool",
        "add enchants flag and attributes flag to hidden item flags of player's tool",
        "remove enchants flag from hidden item flags of player's tool",
        "remove armor trim flag and dye flag from item flags of player's tool",
        "clear item flags of player's tool"})
@Since("INSERT VERSION")
public class ExprHiddenItemFlags extends PropertyExpression<ItemType, ItemFlag> {

    static {
        register(ExprHiddenItemFlags.class, ItemFlag.class, "[hidden] item flags", "itemtypes");
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
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
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return CollectionUtils.array(ItemFlag[].class);
        } else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            for (ItemType itemType : getExpr().getArray(event)) {
                ItemMeta itemMeta = itemType.getItemMeta();
                Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
                itemMeta.removeItemFlags(itemFlags.toArray(new ItemFlag[0]));
                itemType.setItemMeta(itemMeta);
            }
        } else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            if (delta != null && delta instanceof ItemFlag[] itemFlags) {
                for (ItemType itemType : getExpr().getArray(event)) {
                    ItemMeta itemMeta = itemType.getItemMeta();
                    if (mode == ChangeMode.ADD) {
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
    public @NotNull Class<? extends ItemFlag> getReturnType() {
        return ItemFlag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "hidden item flags of " + getExpr().toString(e, d);
    }

}
