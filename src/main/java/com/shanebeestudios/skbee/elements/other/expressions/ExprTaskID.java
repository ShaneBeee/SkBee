package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.sections.SecRunTaskLater;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Task - ID")
@Description("Get the current task ID or all task IDs. This will only be tasks created with the task section.")
@Examples({"set {_ids::*} to all task ids",
        "set {_id} to current task id",
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
@Since("INSERT VERSION")
public class ExprTaskID extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprTaskID.class, Number.class, ExpressionType.SIMPLE,
                "current task id", "all task ids");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        if (this.pattern == 0) {
            for (TriggerSection currentSection : ParserInstance.get().getCurrentSections()) {
                if (currentSection instanceof SecRunTaskLater runTaskLater)
                    return new Number[]{runTaskLater.getCurrentTaskID()};
            }
            return null;
        } else {
            return SecRunTaskLater.getTaskIDs().toArray(new Integer[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return this.pattern == 0;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.pattern == 0 ? "current task id" : "all task ids";
    }

}
