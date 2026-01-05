package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Make Player Break Block")
@Description({"Breaks blocks as if a specific player had broken them using their held item as the tool.",
    "This triggers the on break event, so be careful with infinite loops.",
    "This allows survival mode players to break unbreakable blocks such as bedrock, however, no item will be dropped."})
@Examples({
    "make {_player} break {_blocks::*}",
    "make player break target block"
})
@Since("3.14.0")
public class EffMakePlayerBreakBlock extends Effect {

    static {
        Skript.registerEffect(EffMakePlayerBreakBlock.class,
            "(make|force) %player% break %blocks%");
    }

    private Expression<Player> player;
    private Expression<Block> blocks;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        this.blocks = (Expression<Block>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (Block block : this.blocks.getArray(event)) {
            Player thePlayer = player.getSingle(event);
            if (thePlayer != null) {
                thePlayer.breakBlock(block);
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String playerName = this.player.toString(e, d);
        String blocks = this.blocks.toString(e, d);
        return "make " + playerName + " break " + blocks;
    }

}
