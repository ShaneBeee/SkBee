package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.event.Event;

@Name("EntityBlockStorage - Is Full")
@Description({"Check if an entity storage block is fully of entities.",
        "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+"})
@Examples({"if entity storage of block at player is full:",
        "if entity storage of target block is not full:"})
@Since("1.0.0")
public class CondEntityStorageBlockFull extends Condition {

    static {
        Skript.registerCondition(CondEntityStorageBlockFull.class,
                "entity storage of %block% is full", "entity storage of %block% is(n't| not) full");
    }

    private Expression<Block> block;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parse) {
        setNegated(i == 1);
        this.block = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        Block block = this.block.getSingle(event);
        if (block != null) {
            BlockState state = block.getState();
            if (state instanceof EntityBlockStorage<?>) {
                EntityBlockStorage<?> storage = ((EntityBlockStorage<?>) state);
                return isNegated() != storage.isFull();
            }
        }
        return isNegated();
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
