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
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@Name("Task - Cancel Task")
@Description({"Stop a task by ID.",
    "If running Folia, this effect will do nothing."})
@Examples({"run 0 ticks later repeating every second:",
    "\tset {-id} to current task id",
    "\tadd 1 to {_a}",
    "\tif {_a} > 10:",
    "\t\texit loop",
    "",
    "on break:",
    "\tstop task with id {-id}"})
@Since("3.3.0")
public class EffTaskStop extends Effect {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    static {
        if (!Util.IS_RUNNING_FOLIA) { // Folia does not have task IDs
            Skript.registerEffect(EffTaskStop.class, "(stop|cancel) task[s] with id[s] %numbers%");
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
