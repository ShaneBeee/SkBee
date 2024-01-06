package com.shanebeestudios.skbee.elements.tickmanager.effects;

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

@Name("Server Tick - Step Server")
@Description({"Steps the game a certain amount of ticks if the server is currently frozen.",
        "Steps occur when the server is in a frozen state which can be started by either using",
        "the in game `/tick freeze` command or the `server frozen state` expression.",
        Util.MCWIKI_TICK_COMMAND, "Requires Minecraft 1.20.4+"})
@Examples({"step game if frozen 10 ticks",
        "stop stepping game"})
@Since("INSERT VERSION")
public class EffServerTickStep extends Effect {

    static {
        Skript.registerEffect(EffServerTickStep.class,
                "step (game|server) [if frozen] [by] %timespan%",
                "stop stepping (game|server)");
    }

    private Expression<Timespan> ticks;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.ticks = matchedPattern == 0 ? (Expression<Timespan>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        ServerTickManager tickManager = Bukkit.getServerTickManager();
        if (this.ticks == null) {
            tickManager.stopStepping();
        } else {
            Timespan timespan = this.ticks.getSingle(event);
            if (timespan != null) {
                tickManager.stepGameIfFrozen((int) timespan.getTicks_i());
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.ticks == null) return "stop stepping server";
        return "step server by " + this.ticks.toString(e, d);
    }

}
