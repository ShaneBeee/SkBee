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
@Description({"Force this player to break a Block using the item in their main hand. ",
    "This effect will respect enchantments, handle item durability (if applicable) and drop experience and the correct items according to the tool/item in the player's hand.",
    "This triggers the on break event, so be careful with infinite loops.",
    "This allows survival mode players to break unbreakable blocks such as bedrock, however, no item will be dropped."
})
@Examples({
    "make {_player} break {_blocks::*}",
    "make player break target block"
})
@Since("INSERT VERSION")
public class EffMakePlayerBreakBlock extends Effect {

    static {
        Skript.registerEffect(EffMakePlayerBreakBlock.class,
            "(make|force) %player% [to] break %blocks%");
    }

    private Expression<Player> player;
    private Expression<Block> blocks;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) exprs[0];
        this.blocks = (Expression<Block>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player player = this.player.getSingle(event);
        if (player == null) return;
        this.blocks.stream(event).forEach(player::breakBlock);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "make " + this.player.toString(e, d) + " break " + this.blocks.toString(e, d);
    }

}
