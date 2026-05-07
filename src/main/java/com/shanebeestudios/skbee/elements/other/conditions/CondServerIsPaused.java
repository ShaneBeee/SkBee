package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class CondServerIsPaused extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondServerIsPaused.class,
            "server is paused",
            "server (is not|isn't) paused")
            .name("Server Is Paused")
            .description("Checks whether the server is sleeping/paused.",
                "The server is paused when no players have been online longer than `pause-when-empty-seconds` setting.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (isNegated()) {
            return !Bukkit.getServer().isPaused();
        }
        return Bukkit.getServer().isPaused();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (isNegated()) {
            return "server is not paused";
        }
        return "server is paused";
    }

}
