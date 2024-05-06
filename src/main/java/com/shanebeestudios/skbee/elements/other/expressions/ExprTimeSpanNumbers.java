package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TimeSpan - Numbers")
@Description("Get the ticks/seconds/minutes/hours of a timespan.")
@Examples("set {_ticks} to ticks of {_timespan}")
@Since("1.17.0")
public class ExprTimeSpanNumbers extends SimplePropertyExpression<Timespan, Number> {

    static {
        register(ExprTimeSpanNumbers.class, Number.class,
                "(ticks|1:seconds|2:minutes|3:hours)", "timespan");
    }

    private int pattern;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        setExpr((Expression<? extends Timespan>) exprs[0]);
        return true;
    }

    @Override
    public @Nullable Number convert(Timespan timespan) {
        long ticks = timespan.getTicks_i();
        return switch (pattern) {
            case 1 -> (ticks / 20);
            case 2 -> (ticks / 20 / 60);
            case 3 -> (ticks / 20 / 60 / 60);
            default -> ticks;
        };
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String[] numbers = new String[]{"ticks", "seconds", "minutes", "hours"};
        return "time span " + numbers[pattern];
    }

}
