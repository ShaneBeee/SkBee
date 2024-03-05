package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.other.type.OldItemFlag;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoDoc
@Deprecated // deprecated on Feb 16/2024
public class ExprHiddenItemFlags extends PropertyExpression<ItemType, OldItemFlag> {

    static {
        register(ExprHiddenItemFlags.class, OldItemFlag.class, "hidden item flags", "itemtypes");
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends ItemType>) exprs[0]);
        if (matchedPattern == 0) {
            Skript.warning("'" + parseResult.expr + "' is deprecated, please use new expression: '" +
                    "item[ ]flags of %itemtypes%");
        } else {
            Skript.warning("'" + parseResult.expr + "' is deprecated, please use new expression: '" +
                    "%itemtypes%'[s] item[ ]flags");
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected OldItemFlag @NotNull [] get(Event event, ItemType[] source) {
        List<OldItemFlag> itemFlags = new ArrayList<>();
        for (ItemType itemType : source) {
            itemType.getItemMeta().getItemFlags().forEach(itemFlag -> itemFlags.add(OldItemFlag.getFromBukkit(itemFlag)));
        }
        return itemFlags.toArray(new OldItemFlag[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return CollectionUtils.array(OldItemFlag[].class);
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
            if (delta != null && delta instanceof OldItemFlag[] itemFlags) {
                for (ItemType itemType : getExpr().getArray(event)) {
                    ItemMeta itemMeta = itemType.getItemMeta();
                    if (mode == ChangeMode.ADD) {
                        for (OldItemFlag itemFlag : itemFlags) {
                            itemMeta.addItemFlags(itemFlag.getBukkitItemFlag());
                        }
                    } else {
                        for (OldItemFlag itemFlag : itemFlags) {
                            itemMeta.removeItemFlags(itemFlag.getBukkitItemFlag());
                        }
                    }
                    itemType.setItemMeta(itemMeta);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends OldItemFlag> getReturnType() {
        return OldItemFlag.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "hidden item flags of " + getExpr().toString(e, d);
    }

}
