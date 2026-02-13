package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class EffTaskStop extends Effect {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    public static void register(Registration reg) {
        if (!Util.IS_RUNNING_FOLIA) { // Folia does not have task IDs
            reg.newEffect(EffTaskStop.class, "(stop|cancel) task[s] with id[s] %numbers%")
                .name("Task - Cancel Task")
                .description("Stop a task by ID.",
                    "If running Folia, this effect will do nothing.")
                .examples("run 0 ticks later repeating every second:",
                    "\tset {-id} to current task id",
                    "\tadd 1 to {_a}",
                    "\tif {_a} > 10:",
                    "\t\texit loop",
                    "",
                    "on break:",
                    "\tstop task with id {-id}")
                .since("3.3.0")
                .register();
        }
    }

    private Expression<Number> ids;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.ids = (Expression<Number>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Number id : this.ids.getArray(event)) {
            SCHEDULER.cancelTask(id.intValue());
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "stop task[s] with id[s] " + this.ids.toString(e, d);
    }

}
