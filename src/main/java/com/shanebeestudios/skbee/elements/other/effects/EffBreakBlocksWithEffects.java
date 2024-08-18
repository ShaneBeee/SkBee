package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Name("Break Blocks with Effects")
@Description({"Breaks blocks as if a player had broken them. Will drop items, play particles and sounds. Requires PaperMC.",
    "Optionally you can trigger it to drop experience as well.",
    "Optionally you can include an item which is used to determine which drops the block will drop."})
@Examples({"break blocks in radius 2 around target block with effects",
    "break {_blocks::*} with effects and with xp",
    "break {_blocks::*} with effects and with xp using player's tool"})
@Since("3.6.1")
public class EffBreakBlocksWithEffects extends Effect {

    private static final boolean HAS_EFFECTS = Skript.methodExists(Block.class, "breakNaturally", boolean.class, boolean.class);

    static {
        Skript.registerEffect(EffBreakBlocksWithEffects.class,
            "break %blocks% [naturally] with effects [exp:[and] with (experience|exp|xp)] [using %-itemtype%]");
    }

    private boolean exp;
    private Expression<Block> blocks;
    private Expression<ItemType> itemType;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_EFFECTS) {
            Skript.error("'break %blocks% with effects' requires PaperMC. Use Skript's 'break %blocks%' effect instead.");
            return false;
        }
        this.exp = parseResult.hasTag("exp");
        this.blocks = (Expression<Block>) exprs[0];
        this.itemType = (Expression<ItemType>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        ItemStack itemStack = null;
        if (this.itemType != null) {
            ItemType it = this.itemType.getSingle(event);
            if (it != null) itemStack = it.getRandom();
        }

        for (Block block : this.blocks.getArray(event)) {
            if (itemStack != null) block.breakNaturally(itemStack, true, this.exp);
            else block.breakNaturally(true, this.exp);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String blocks = this.blocks.toString(e, d);
        String xp = this.exp ? " and with experience" : "";
        String it = this.itemType != null ? (" using " + this.itemType.toString(e, d)) : "";
        return "break " + blocks + " naturally with effects" + xp + it;
    }

}
