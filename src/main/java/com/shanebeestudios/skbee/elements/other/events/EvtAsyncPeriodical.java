package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.events.bukkit.ScheduledEvent;
import ch.njol.skript.events.bukkit.ScheduledNoWorldEvent;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.scheduling.TaskUtils;
import com.github.shanebeee.skr.scheduling.scheduler.Scheduler;
import com.github.shanebeee.skr.scheduling.scheduler.task.Task;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Mostly copied from {@link ch.njol.skript.events.EvtPeriodical} with a few changes for async/TaskUtils
 */
public class EvtAsyncPeriodical extends SkriptEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtAsyncPeriodical.class, ScheduledNoWorldEvent.class,
                "every %timespan% async [in [world[s]] %-worlds%]",
                "async every %timespan% [in [world[s]] %-worlds%]")
            .name("Async Periodical")
            .description("An event that is asynchronously called periodically.")
            .examples("every 2 seconds async:",
                "every minecraft hour async:",
                "every tick async: # can cause lag depending on the code inside the event",
                "every 3 minecraft days async:",
                "every 2 seconds async in \"world\":")
            .since("INSERT VERSION")
            .register();
    }

    private Timespan period;
    private World @Nullable [] worlds;
    private Task<?>[] tasks;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.period = ((Literal<Timespan>) args[0]).getSingle();
        if (args.length > 1 && args[1] != null) {
            this.worlds = ((Literal<World>) args[1]).getArray();
        }
        return true;
    }

    @Override
    public boolean postLoad() {
        Scheduler<?> globalScheduler = TaskUtils.getGlobalScheduler();
        long ticks = this.period.getAs(Timespan.TimePeriod.TICK);

        if (this.worlds == null) {
            this.tasks = new Task[]{
                globalScheduler.runTaskTimerAsync(() -> execute(null), ticks, ticks)
            };
        } else {
            this.tasks = new Task[this.worlds.length];

            for (World world : this.worlds) {
                globalScheduler.runTaskTimerAsync(() -> execute(world), ticks - (world.getFullTime() % ticks), ticks);
            }
        }

        return true;
    }

    private void execute(@Nullable World world) {
        ScheduledEvent event = world == null ? new ScheduledNoWorldEvent() : new ScheduledEvent(world);
        this.trigger.execute(event);
    }

    @Override
    public void unload() {
        for (Task<?> task : this.tasks) {
            task.cancel();
        }
    }

    @Override
    public boolean check(Event event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEventPrioritySupported() {
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "every " + this.period;
    }

}
