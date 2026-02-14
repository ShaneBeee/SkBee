package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffBlockRandomlyTick extends Effect {

    public static void register(Registration reg){
        reg.newEffect(EffBlockRandomlyTick.class, "random[ly] tick %blocks%")
            .name("Random Tick Block")
            .description("Causes the block to be ticked randomly.",
                "This will tick the block the same way Minecraft randomly ticks according to the randomTickSpeed gamerule.",
                "Requires Paper 1.19+")
            .examples("random tick blocks in radius 3 around target block")
            .since("3.0.0")
            .register();
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
        for (Block block : this.blocks.getArray(event)) {
            block.randomTick();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "randomly tick " + this.blocks.toString(e, d);
    }

}
