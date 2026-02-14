package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprLastRuntimeLogs extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprLastRuntimeLogs.class, String.class,
                "[the] [last] runtime logs")
            .noDoc()
            .register();
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
