package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Task - Run Task Later")
@Description({"Run a task later. Similar to Skript's delay effect, with the difference being everything in the",
        "section is run later. All code after your section will keep running as normal without a delay.",
        "This can be very useful in loops, to prevent halting the loop.",
        "You can optionally have your task repeat until cancelled.",
        "You can optionally run your code async/on another thread.",
        "\nNOTE: A good chunk of Bukkit/Minecraft stuff can NOT be run async. It may throw console errors.",
        "Please be careful when running async, this is generally reserved for heavy math/functions that could cause lag.",
        "Simply waiting a tick, or running a new non-async section will put your code back on the main thread."})
@Examples({"on explode:",
        "\tloop exploded blocks:",
        "\t\tset {_loc} to location of loop-block",
        "\t\tset {_data} to block data of loop-block",
        "\t\trun 2 seconds later:",
        "\t\t\tset block at {_loc} to {_data}\n",
        "",
        "run 0 ticks later repeating every second:",
        "\tadd 1 to {_a}",
        "\tif {_a} > 10:",
        "\t\tstop current task"})
@Since("3.0.0")
public class SecRunTaskLater extends Section {

    private static final Plugin PLUGIN = SkBee.getPlugin();
    private static final List<BukkitTask> TASKS = new ArrayList<>();

    public static void cancelTasks() {
        TASKS.forEach(BukkitTask::cancel);
        TASKS.clear();
    }

    public static List<Integer> getTaskIDs() {
        List<Integer> taskIDs = new ArrayList<>();
        TASKS.forEach(task -> taskIDs.add(task.getTaskId()));
        return taskIDs;
    }

    static {
        Skript.registerSection(SecRunTaskLater.class,
                "[:async] (run|execute) [task] %timespan% later [repeating every %-timespan%]");
    }

    private boolean async;
    private Expression<Timespan> timespan;
    private Expression<Timespan> repeating;
    private Trigger trigger;
    private BukkitTask currentTask;

    @SuppressWarnings({"NullableProblems", "unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.async = parseResult.hasTag("async");
        this.timespan = (Expression<Timespan>) exprs[0];
        this.repeating = (Expression<Timespan>) exprs[1];
        this.trigger = loadCode(sectionNode, "run later", ParserInstance.get().getCurrentEvents());
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        Timespan timespan = this.timespan.getSingle(event);
        long delay = timespan != null ? timespan.getTicks_i() : 0;

        long repeat = 0;
        if (this.repeating != null) {
            Timespan repeatingTimespan = this.repeating.getSingle(event);
            if (repeatingTimespan != null) repeat = repeatingTimespan.getTicks_i();
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Runnable runnable = () -> {
            ParserInstance.get().getCurrentSections().add(this);
            Variables.setLocalVariables(event, localVars);
            TriggerItem.walk(trigger, event);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(event));
            Variables.removeLocals(event);
            ParserInstance.get().getCurrentSections().remove(this);
        };

        if (repeat > 0 && async) {
            this.currentTask = scheduler.runTaskTimerAsynchronously(PLUGIN, runnable, delay, repeat);
        } else if (repeat > 0) {
            this.currentTask = scheduler.runTaskTimer(PLUGIN, runnable, delay, repeat);
        } else if (async) {
            this.currentTask = scheduler.runTaskLaterAsynchronously(PLUGIN, runnable, delay);
        } else {
            this.currentTask = scheduler.runTaskLater(PLUGIN, runnable, delay);
        }
        TASKS.add(this.currentTask);
        return super.walk(event, false);
    }

    public int getCurrentTaskID() {
        return this.currentTask.getTaskId();
    }

    public void stopCurrentTask() {
        this.currentTask.cancel();
        TASKS.remove(this.currentTask);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String async = this.async ? "async " : "";
        String repeat = this.repeating != null ? (" repeating every " + this.repeating.toString(e, d)) : "";
        return async + "run task " + this.timespan.toString(e, d) + " later" + repeat;
    }

}
