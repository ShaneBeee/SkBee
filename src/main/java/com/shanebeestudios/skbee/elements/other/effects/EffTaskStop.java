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
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Task - Cancel Task")
@Description({"Stop tasks.",
    "`stop all tasks` = Will stop all currently running tasks created with a task section.",
    "`stop current task` = Will stop the task section this effect is in.",
    "`stop task with id` = Will stop any task from an ID."})
@Examples({"run 0 ticks later repeating every second:",
    "\tset {-id} to current task id",
    "\tadd 1 to {_a}",
    "\tif {_a} > 10:",
    "\t\tstop current task",
    "",
    "on unload:",
    "\tstop all tasks",
    "",
    "on break:",
    "\tstop task with id {-id}"})
@Since("3.3.0")
public class EffTaskStop extends Effect {

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    static {
        Skript.registerEffect(EffTaskStop.class,
            "(stop|cancel) all tasks", "(stop|cancel) current task", "(stop|cancel) task[s] with id[s] %numbers%");
    }

    private int pattern;
    private Expression<Number> ids;
    private SecRunTaskLater currentTask;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (matchedPattern == 2) {
            this.ids = (Expression<Number>) exprs[0];
        } else if (matchedPattern == 1) {
            List<SecRunTaskLater> currentSections = getParser().getCurrentSections(SecRunTaskLater.class);
            if (currentSections.isEmpty()) {
                Skript.error("'" + parseResult.expr + "' can only be used in a run task section.");
                return false;
            }
            this.currentTask = currentSections.getLast();
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        switch (this.pattern) {
            case 0 -> SecRunTaskLater.cancelTasks();
            case 1 -> this.currentTask.stopCurrentTask();
            case 2 -> {
                for (Number id : this.ids.getArray(event)) {
                    SCHEDULER.cancelTask(id.intValue());
                }
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return switch (this.pattern) {
            case 1 -> "stop current task";
            case 2 -> "stop task[s] with id[s] " + this.ids.toString(e, d);
            default -> "stop all tasks";
        };
    }

}
