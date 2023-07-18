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
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawner - Reset Timer")
@Description({"Resets the spawner timer for a spawner.",
        "Requires a PaperMC server"})
@Examples({"on spawner spawn:",
        "\treset spawner timer"})
@Since("INSERT VERSION")
public class EffSpawnerResetTimer extends Effect {

    static {
        Skript.registerEffect(EffSpawnerResetTimer.class, "reset spawner timer [of %blocks%]");
    }

    private static final boolean SUPPORTS_RESET_TIMER = Skript.methodExists(CreatureSpawner.class, "resetTimer");
    private Expression<Block> blocks;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!SUPPORTS_RESET_TIMER) {
            Skript.error("The reset timer effect requires a PaperMC server.");
            return false;
        }
        this.blocks = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Block block : blocks.getArray(event)) {
            if (block.getState() instanceof CreatureSpawner spawner) {
                spawner.resetTimer();
                spawner.update();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "reset timer of " + blocks.toString(event, debug);
    }

}
