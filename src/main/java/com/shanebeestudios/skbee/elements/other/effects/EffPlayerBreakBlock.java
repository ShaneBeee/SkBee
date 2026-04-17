package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffPlayerBreakBlock extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffPlayerBreakBlock.class,
                "make %player% break %blocks%")
            .name("Player Break Block")
            .description("Forces a player to break a Block using the item in their main hand.",
                "This effect will respect enchantments, handle item durability (if applicable) and drop experience " +
                    "and the correct items according to the tool/item in the player's hand.",
                "Note that this method will call a block break event, " +
                    "meaning that this method may not be successful in breaking the block if the event was cancelled by a third party plugin.",
                "Care should be taken if running this method in a block break event listener as recursion may be possible " +
                    "if it is invoked on the same block being broken in the event.")
            .examples("make player break target block",
                "make player break blocks within {_loc1} and {_loc2}")
            .register();
    }

    private Expression<Player> player;
    private Expression<Block> blocks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.player = (Expression<Player>) expressions[0];
        this.blocks = (Expression<Block>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player player = this.player.getSingle(event);
        if (player == null) return;

        for (Block block : this.blocks.getArray(event)) {
            if (block.getWorld() != player.getWorld()) continue;

            player.breakBlock(block);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "make " + this.player.toString(event, debug) + " break " + this.blocks.toString(event, debug);
    }

}
