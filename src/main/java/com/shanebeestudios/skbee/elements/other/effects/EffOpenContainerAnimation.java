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
import org.bukkit.block.Lidded;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Open Container Animation")
@Description({"Play the open/close animation on a lidded block (ie: chest, barrel or shulker box).",
    "Note: When using the open method, the block will basically be locked 'open', a player opening/closing the block will not close the lid.",
    "Requires Minecraft 1.16+"})
@Examples({"play open animation on target block",
    "play close animation on all blocks in radius 3 around player"})
@Since("1.10.0")
public class EffOpenContainerAnimation extends Effect {

    static {
        Skript.registerEffect(EffOpenContainerAnimation.class,
            "play (:open|close) animation on %blocks%");
    }

    private boolean open;
    private Expression<Block> blocks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.open = parseResult.hasTag("open");
        this.blocks = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Block block : this.blocks.getArray(event)) {
            if (block.getState() instanceof Lidded lidded) {
                if (open) {
                    lidded.open();
                } else {
                    lidded.close();
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("play %s animation on %s", open ? "open" : "close", this.blocks.toString(e, d));
    }

}
