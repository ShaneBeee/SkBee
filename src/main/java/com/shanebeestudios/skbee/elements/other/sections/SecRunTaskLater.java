package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.LoopSection;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.scheduler.Scheduler;
import com.shanebeestudios.skbee.api.scheduler.TaskUtils;
import com.shanebeestudios.skbee.api.scheduler.task.Task;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Name("Task - Run Task Later")
@Description({"Run a task later. Similar to Skript's delay effect, with the difference being everything in the",
    "section is run later. All code after your section will keep running as normal without a delay.",
    "This can be very useful in loops, to prevent halting the loop.",
    "You can optionally have your task repeat until cancelled.",
    "You can optionally run your code async/on another thread.",
    "\nNOTE: A good chunk of Bukkit/Minecraft stuff can NOT be run async. It may throw console errors.",
    "Please be careful when running async, this is generally reserved for heavy math/functions that could cause lag.",
    "Simply waiting a tick, or running a new non-async section will put your code back on the main thread.",
    "",
    "**Patterns**:",
    "The 2nd pattern is only of concern if you are running Folia or have Paper schedulers enabled in the config, " +
        "otherwise just use the first pattern.",
    "- `globally` = Will run this task on the global scheduler.",
    "- `for %entity` = Will run this task for an entity, will follow the entity around (region wise)" +
        "and will cancel itself when the entity is no longer valid.",
    "- `at %location%` = Will run this task at a specific location (Use this for block changes in this section)."})
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
    "\t\texit loop"})
@Since("3.0.0")
public class SecRunTaskLater extends LoopSection {

    static {
        Skript.registerSection(SecRunTaskLater.class,
            "[:async] (run|execute) [task] %timespan% later [repeating every %-timespan%] [globally]",
            "[:async] (run|execute) [task] %timespan% later [repeating every %-timespan%] [(at|on|for) %-entity/location%]");
    }

    private boolean async;
    private Expression<Timespan> timespan;
    private Expression<?> taskObject;
    private Expression<Timespan> repeating;
    private Task<?> task;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.async = parseResult.hasTag("async");
        this.timespan = (Expression<Timespan>) exprs[0];
        if (matchedPattern == 1) {
            this.taskObject = exprs[2];
        }
        this.repeating = (Expression<Timespan>) exprs[1];
        ParserInstance parserInstance = ParserInstance.get();
        Kleenean hasDelayBefore = parserInstance.getHasDelayBefore();
        parserInstance.setHasDelayBefore(Kleenean.TRUE);
        loadCode(sectionNode);
        parserInstance.setHasDelayBefore(hasDelayBefore);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Timespan timespan = this.timespan.getSingle(event);
        long delay = timespan != null ? timespan.getAs(Timespan.TimePeriod.TICK) : 0;

        long repeat = 0;
        if (this.repeating != null) {
            Timespan repeatingTimespan = this.repeating.getSingle(event);
            if (repeatingTimespan != null) repeat = repeatingTimespan.getAs(Timespan.TimePeriod.TICK);
        }

        AtomicReference<Object> previousLocalVars = new AtomicReference<>(Variables.copyLocalVariables(event));
        Runnable runnable = () -> {
            Variables.setLocalVariables(event, previousLocalVars.get());
            assert this.first != null;
            TriggerItem.walk(this.first, event);
            previousLocalVars.set(Variables.copyLocalVariables(event));
        };

        Scheduler<?> scheduler;
        if (this.taskObject != null) {
            Object object = this.taskObject.getSingle(event);
            //noinspection IfCanBeSwitch // requires java 21+
            if (object instanceof Entity entity) scheduler = TaskUtils.getEntityScheduler(entity);
            else if (object instanceof Location location) scheduler = TaskUtils.getRegionalScheduler(location);
            else scheduler = TaskUtils.getGlobalScheduler();
        } else {
            scheduler = TaskUtils.getGlobalScheduler();
        }
        if (scheduler == null) return super.walk(event, false);

        if (repeat > 0 && this.async) {
            this.task = scheduler.runTaskTimerAsync(runnable, delay, repeat);
        } else if (repeat > 0) {
            this.task = scheduler.runTaskTimer(runnable, delay, repeat);
        } else if (this.async) {
            this.task = scheduler.runTaskLaterAsync(runnable, delay);
        } else {
            this.task = scheduler.runTaskLater(runnable, delay);
        }
        if (last != null) last.setNext(null);
        return super.walk(event, false);
    }

    public int getCurrentTaskId() {
        if (this.task.isCancelled()) return -1;
        return this.task.getTaskId();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String async = this.async ? "async " : "";
        String type = this.taskObject != null ? (" for " + this.taskObject.toString(e, d)) : " globally";
        String repeat = this.repeating != null ? (" repeating every " + this.repeating.toString(e, d)) : "";
        return async + "run task " + this.timespan.toString(e, d) + " later" + repeat + type;
    }

    @Override
    public TriggerItem getActualNext() {
        return null;
    }

    @Override
    public void exit(Event event) {
        this.task.cancel();
    }

}
