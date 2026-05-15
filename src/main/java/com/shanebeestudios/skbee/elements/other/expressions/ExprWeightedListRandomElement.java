package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.WeightedList;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class ExprWeightedListRandomElement extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprWeightedListRandomElement.class, Object.class,
                "[%-number%] random weighted element[s] (of|from) %weightedlist%")
            .name("WeightedList - Random Element")
            .description("Returns a random element from the provided weighted list.",
                "Optionally you can include an amount of multiple random weighted elements from the list.")
            .examples("set {_loot} to new weighted list",
                "add 1 of salmon to {_loot} with weight 5",
                "add 1 of cod to {_loot} with weight 7",
                "add stone sword of unbreaking 3 to {_loot} with weight 1",
                "add 3 of cooked chicken to {_loot} with weight 2",
                "add 4 of string to {_loot} with weight 15",
                "",
                "set {_random} to random weighted element of {_loot}",
                "set {_random::*} to 5 random weighted elements of {_loot}\n")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Number> amount;
    private Expression<WeightedList> weightedList;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.amount = (Expression<Number>) expressions[0];
        this.weightedList = (Expression<WeightedList>) expressions[1];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        WeightedList list = this.weightedList.getSingle(event);
        if (list == null) {
            return null;
        }

        if (this.amount != null) {
            Number number = this.amount.getSingle(event);
            if (number == null) {
                return null;
            }
            int amount = number.intValue();
            Object[] results = new Object[amount];
            for (int i = 0; i < amount; i++) {
                results[i] = list.nextEntry();
            }
            return results;
        }

        return new Object[]{list.nextEntry()};
    }

    @Override
    public boolean isSingle() {
        return this.amount == null;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String amount = this.amount != null ? this.amount.toString(event, debug) + " " : "";
        return amount + "random weighted element[s] from " + this.weightedList.toString(event, debug);
    }

}
