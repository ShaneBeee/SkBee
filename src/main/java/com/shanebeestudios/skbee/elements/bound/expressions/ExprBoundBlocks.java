package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - Blocks")
@Description("All the blocks or specific type of blocks within a bound")
@Examples({"set {_blocks::*} to all blocks within bound {bound}",
        "set {_blocks::*} to all blocks within bound bound with id \"le-bound\"",
        "set {_stone blocks::*} to all blocks of type stone block with bound {_bound}",
        "set all blocks within bound {bound} to stone",
        "loop all blocks within bound {bound}:",
        "\tif loop-block is stone:",
        "\t\tset loop-block to grass"})
@Since("1.0.0, INSERT VERSION (specific blocks)")
public class ExprBoundBlocks extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprBoundBlocks.class, Block.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] blocks [of type %-itemtypes%] within bound %bound%");
    }

    private Expression<Bound> bound;
    private Expression<ItemType> types;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        bound = (Expression<Bound>) exprs[1];
        if (exprs[0] != null)
            types = ((Expression<ItemType>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Block[] get(Event event) {
        Bound single = bound.getSingle(event);
        if (single == null) {
            return null;
        }
        List<Block> blocks;
        if (types != null) {
            blocks = new ArrayList<>();
            for (ItemType itemType : types.getArray(event)) {
                Material material = itemType.getMaterial();
                if (!material.isBlock()) continue;
                blocks.addAll(new ArrayList<>(single.getBlocks(material)));
            }
        }
        else {
            blocks = new ArrayList<>(single.getBlocks());
        }
        return blocks.toArray(new Block[0]);
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        String types = "";
        if (this.types != null)
            types = " of type " + this.types.toString(event, debug);
        return "the blocks" + types + " within bound " + bound.toString(event, debug);
    }

}
