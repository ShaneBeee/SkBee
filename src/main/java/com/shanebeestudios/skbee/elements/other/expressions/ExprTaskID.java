package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Task - ID")
@Description({"Get the current task ID. This will only be tasks created with the task section.",
    "If running Folia, tasks do not have IDs and this will always return `-1`."})
@Examples({"set {_id} to current task id",
    "",
    "run 0 ticks later repeating every second:",
    "\tset {-id} to current task id",
    "\tadd 1 to {_a}",
    "\tif {_a} > 10:",
    "\t\texit loop",
    "",
    "on unload:",
    "\tstop all tasks",
    "",
    "on break:",
    "\tstop task with id {-id}"})
@Since("3.3.0")
public class ExprTaskID extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprTaskID.class, Number.class, ExpressionType.SIMPLE, "current task id");
    }

    private SecRunTaskLater task;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        List<SecRunTaskLater> currentSections = getParser().getCurrentSections(SecRunTaskLater.class);
        this.task = currentSections.getLast();
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        return new Number[]{this.task.getCurrentTaskId()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "current task id";
    }

}
