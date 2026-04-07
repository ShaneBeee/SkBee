package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprParseCondition extends SimpleExpression<Boolean> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprParseCondition.class, Boolean.class,
            "parse condition[s] %strings%")
            .name("Parse Condition - With Return")
            .description("This will parse a string as a condition and then check it and returns whether or not it's valid.",
                "If you provide a command sender it works the same as Skript's 'effect commands'.",
                "Otherwise it runs using the current event allowing you to use event-values",
                "",
                "**NOTE:** This is handled very differently from the parse effect expression in addition to behaving differently than Skript's `whether <condition>`.",
                "If you have no good reason to use this, please check out the other two.",
                "*tip: when running into invalid null states try replacing parts with variables*")
            .examples("command /parse <string>:",
                "\ttrigger:",
                "\t\tif parse condition arg-1 is false:",
                "\t\t\tsend \"&4:ERROR&c %arg-1% did not pass\"")
            .since("3.19.0")
            .register();
    }

    private Expression<String> conditions;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.conditions = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Boolean @Nullable [] get(Event event) {
        String[] conditions = this.conditions.getArray(event);
        Boolean[] booleans = new Boolean[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            Condition condition = Condition.parse(conditions[i], null);
            booleans[i] = condition != null && TriggerItem.walk(condition, event);
        }
        return booleans;
    }

    @Override
    public boolean isSingle() {
        return this.conditions.isSingle();
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "parse condition '" + this.conditions.toString(e, d) + "'";
    }

}
