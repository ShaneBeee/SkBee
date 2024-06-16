package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
@Name("NBT - Item from NBT")
@Description({"This expression allows you to grab an item from NBT compounds.",
    "This can be useful when wanting to grab items from file nbt, or nbt of an entity or an inventory holding block (like a chest or furnace).",
    "It can also be useful for creating your own serializing system.",
    "NOTE: Items previously serialized in MC versions 1.20.4 and below, will properly upgrade for MC 1.20.5."})
@Examples({"set {_nbt::*} to compound list tag \"Inventory\" of file nbt of \"world/playerdata/some-players-uuid.dat\"",
    "loop {_nbt::*}",
    "\tset {_i} to item from nbt loop-value"})
@Since("1.4.10")
public class ExprItemFromNBT extends PropertyExpression<NBTCompound, ItemType> {

    static {
        Skript.registerExpression(ExprItemFromNBT.class, ItemType.class, ExpressionType.PROPERTY,
            "item[s] (from|of) nbt[s] %nbtcompounds%",
            "nbt item[s] (from|of) %nbtcompounds%");
    }

    @SuppressWarnings({"null", "unchecked"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean i, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends NBTCompound>) exprs[0]);
        return true;
    }

    @Override
    protected ItemType @NotNull [] get(@NotNull Event event, NBTCompound @NotNull [] source) {
        return get(source, nbtCompound -> {
            if (nbtCompound.hasTag("id")) {
                ItemStack itemStack = NBTItem.convertNBTtoItem(nbtCompound);
                if (itemStack != null) return new ItemType(itemStack);
            }
            return null;
        });
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "item from nbt " + getExpr().toString(e, d);
    }

}
