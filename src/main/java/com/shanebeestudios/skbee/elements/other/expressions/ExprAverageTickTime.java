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
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Average Tick Time")
@Description("Represents the average amount of time (in milliseconds) it takes for the server to finish a tick. Requires PaperMC.")
@Examples({"set {_avg} to average tick time",
    "if average tick time > 40:"})
@Since("INSERT VERSION")
public class ExprAverageTickTime extends SimpleExpression<Number> {

    static {
        if (Skript.methodExists(Bukkit.class, "getAverageTickTime")) {
            Skript.registerExpression(ExprAverageTickTime.class, Number.class, ExpressionType.SIMPLE,
                "average tick time");
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Number[] get(Event event) {
        return new Number[]{Bukkit.getAverageTickTime()};
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
    public @NotNull String toString(Event e, boolean d) {
        return "average tick time";
    }

}
