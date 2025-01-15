package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class ExprLastRuntimeLogs extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprLastRuntimeLogs.class, String.class, ExpressionType.SIMPLE,
            "[the] [last] runtime logs");
    }

    public static String[] errors;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected String @Nullable [] get(Event event) {
        return ExprLastRuntimeLogs.errors;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the last runtime logs";
    }

}
