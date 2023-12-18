package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Server Tick - Sprint")
@Description({"Attempts to initiate a sprint, which executes all server ticks at a faster rate then normal.",
        Util.MCWIKI_TICK_COMMAND, "Requires Minecraft 1.20.4+"})
@Examples({"request game to sprint 10 ticks",
        "stop sprinting game"})
@Since("INSERT VERSION")
public class EffServerTickSprint extends Effect {

    static {
        if (Skript.classExists("org.bukkit.ServerTickManager")) {
            Skript.registerEffect(EffServerTickSprint.class,
                    "request (game|server) to sprint %timespan%",
                    "stop sprinting (game|server)");
        }
    }

    private Expression<Timespan> ticks;
    private boolean stop;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.stop = matchedPattern == 1;
        if (!this.stop) {
            this.ticks = (Expression<Timespan>) exprs[0];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        ServerTickManager tickManager = Bukkit.getServerTickManager();
        if (this.stop) {
            tickManager.stopSprinting();
        } else {
            Timespan timespan = this.ticks.getSingle(event);
            if (timespan != null) {
                tickManager.requestGameToSprint((int) timespan.getTicks_i());
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.stop) return "stop stepping server";
        return "request game to sprint by " + this.ticks.toString(e, d);
    }

}
