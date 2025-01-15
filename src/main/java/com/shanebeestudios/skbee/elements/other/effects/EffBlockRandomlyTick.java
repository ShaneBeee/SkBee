package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Random Tick Block")
@Description({"Causes the block to be ticked randomly.",
    "This will tick the block the same way Minecraft randomly ticks according to the randomTickSpeed gamerule.",
    "Requires Paper 1.19+"})
@Examples("random tick blocks in radius 3 around target block")
@Since("3.0.0")
public class EffBlockRandomlyTick extends Effect {

    static {
        Skript.registerEffect(EffBlockRandomlyTick.class, "random[ly] tick %blocks%");
    }

    private Expression<Block> blocks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Block.class, "randomTick")) {
            Skript.error("`random tick` effect requires Paper 1.19+");
            return false;
        }
        this.blocks = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        Block[] blocks = this.blocks.getArray(event);
        if (blocks == null) {
            error("Blocks is empty: " + this.blocks.toString(event, true));
            return;
        }
        for (Block block : blocks) {
            block.randomTick();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "randomly tick " + this.blocks.toString(e, d);
    }

}
