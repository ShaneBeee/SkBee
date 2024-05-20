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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Task - ID")
@Description({"Get the current task ID. This will only be tasks created with the task section.",
        "THIS IS BROKEN... DO NOT USE!"})
@Examples({"set {_id} to current task id",
        "",
        "run 0 ticks later repeating every second:",
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
public class ExprTaskID extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprTaskID.class, Number.class, ExpressionType.SIMPLE, "current task id");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Skript.error("'" + parseResult.expr + "' is currently broken!");
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        return null;
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
