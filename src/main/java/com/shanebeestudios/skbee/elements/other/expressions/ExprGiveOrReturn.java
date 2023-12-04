package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
@Since("INSERT VERSION")
public class ExprGiveOrReturn extends SimpleExpression<ItemStack> {

    static {
        Skript.registerExpression(ExprGiveOrReturn.class, ItemStack.class, ExpressionType.COMBINED,
                "(give|add) or return %itemstacks% to %inventories%");
    }

    private Expression<ItemStack> itemStacks;
    private Expression<Inventory> inventories;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.itemStacks = (Expression<ItemStack>) exprs[0];
        this.inventories = (Expression<Inventory>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemStack[] get(Event event) {
        ItemStack[] itemStacks = this.itemStacks.getArray(event);

        List<ItemStack> returns = new ArrayList<>();
        for (Inventory inventory : this.inventories.getArray(event)) {
            HashMap<Integer, ItemStack> leftOvers = inventory.addItem(itemStacks);
            if (!leftOvers.isEmpty()) {
                returns.addAll(leftOvers.values());
            }
        }
        return returns.toArray(new ItemStack[0]);
    }

    @Override
    public boolean isSingle() {
        return this.itemStacks.isSingle() && this.inventories.isSingle();
    }

    @Override
    public @NotNull Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "give or return " + this.itemStacks.toString(e, d) + " to " + this.inventories.toString(e, d);
    }

}
