package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Name("Give or Return Items")
@Description({"Attempts to add items to an inventory and will return a list of items that did not fit in the inventory."})
@Examples({"set {_i} to give or return diamond to player",
    "if {_i} is set:",
    "\tdrop {_i} at player",
    "",
    "set {_i::*} to add or return (a diamond and an emerald) to inventory of target block",
    "if {_i::*} is set:",
    "\tdrop {_i::*} above target block without velocity"})
@Since("3.0.0")
public class ExprGiveOrReturn extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprGiveOrReturn.class, ItemType.class, ExpressionType.COMBINED,
            "(give|add) or return %itemtypes% to %inventories%");
    }

    private Expression<ItemType> itemTypes;
    private Expression<Inventory> inventories;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemTypes = (Expression<ItemType>) exprs[0];
        this.inventories = (Expression<Inventory>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemType[] get(Event event) {
        List<ItemStack> itemStacks = ItemUtils.addItemTypesToList(Arrays.asList(this.itemTypes.getArray(event)), null);
        ItemStack[] itemStacksArray = itemStacks.toArray(itemStacks.toArray(new ItemStack[0]));

        List<ItemType> returns = new ArrayList<>();
        for (Inventory inventory : this.inventories.getArray(event)) {
            HashMap<Integer, ItemStack> leftOvers = inventory.addItem(itemStacksArray);
            if (!leftOvers.isEmpty()) {
                leftOvers.values().forEach(itemStack -> returns.add(new ItemType(itemStack)));
            }
        }
        return returns.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return this.itemTypes.isSingle() && this.inventories.isSingle();
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "give or return " + this.itemTypes.toString(e, d) + " to " + this.inventories.toString(e, d);
    }

}
