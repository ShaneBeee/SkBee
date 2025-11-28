package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Name("Apply Lock to Block")
@Description("Apply an item as a lock for a block. Requires Minecraft 1.21.2+")
@Examples({"apply lock to target block using player's tool",
    "apply lock to {_blocks::*} using stick named \"Mr Locky\"",
    "remove lock from target block"})
@Since("3.6.2")
public class EffBlockLock extends Effect {

    static {
        Skript.registerEffect(EffBlockLock.class,
            "apply lock to %blocks% using %itemstack%",
            "(remove|clear) lock (of|from) %blocks%");
    }

    private Expression<Block> blocks;
    private Expression<ItemStack> item;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.blocks = (Expression<Block>) exprs[0];
        if (matchedPattern == 0) {
            this.item = (Expression<ItemStack>) exprs[1];
        }
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected void execute(Event event) {
        ItemStack itemStack = this.item != null ? this.item.getSingle(event) : null;

        for (Block block : this.blocks.getArray(event)) {
            BlockState state = block.getState();
            if (state instanceof Lockable lockable) {
                lockable.setLockItem(itemStack);
                state.update(true);
            } else {
                error("Block is not lockable: " + Classes.toString(block.getType()));
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        if (this.item == null) {
            return "remove lock from " + this.blocks.toString(e, d);
        }
        return "apply lock to " + this.blocks.toString(e, d) + " using " + this.item.toString(e, d);
    }

}
