package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Task - Cancel Task")
@Description("Stop tasks.")
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
                "stop all tasks", "stop current task", "stop task[s] with id[s] %numbers%");
    }

    private int pattern;
    private Expression<Number> ids;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (matchedPattern == 2) {
            this.ids = (Expression<Number>) exprs[0];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        switch (this.pattern) {
            case 0 -> SecRunTaskLater.cancelTasks();
            case 1 -> {
                for (TriggerSection currentSection : ParserInstance.get().getCurrentSections()) {
                    if (currentSection instanceof SecRunTaskLater runTaskLater) runTaskLater.stopCurrentTask();
                }
            }
            case 2 -> {
                for (Number id : this.ids.getArray(event)) {
                    SCHEDULER.cancelTask(id.intValue());
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return switch (this.pattern) {
            case 1 -> "stop current task";
            case 2 -> "stop task[s] with id[s] " + this.ids.toString(e, d);
            default -> "stop all tasks";
        };
    }

}
