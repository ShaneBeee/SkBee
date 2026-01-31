package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Parse Condition - With Return")
@Description({"This will parse a string as a condition and then check it and returns whether or not its valid.",
    "If you provide a command sender it works the same as Skript's 'effect commands'.",
    "Otherwise it runs using the current event allowing you to use event-values",
    "",
    "**NOTE:** This is handled very from the parse effect eapresssion in addition to behaving differently than skript's `whether <condition>`",
    "If you have no good reason to use this, please check out the other two.",
    "*tip: when running into invalid null states try replacing parts with variables*"})
@Examples({"command /parse <string>:",
    "\ttrigger:",
    "\t\tif parse condition arg-1 is false:",
    "\t\t\tsend \"&4:ERROR&c %arg-1% did not pass\""})
@Since("2.15.0")
public class ExprParseCondition extends SimpleExpression<Boolean> {

    static {
        Skript.registerExpression(ExprParseCondition.class, Boolean.class, ExpressionType.COMBINED,
            "parse condition[s] %strings%");
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
        return "parse condition `" + this.conditions.toString(e, d) + "'";
    }

}
