package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.WeightedList;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class EffWeightedListAddEntry extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffWeightedListAddEntry.class,
                "add %object% to %weightedlist% with weight %number%")
            .name("WeightedList - Add Entry")
            .description("Adds an entry to a weighted list with a specified weight.",
                "Weight must be greater than 0.")
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

    private Expression<?> object;
    private Expression<WeightedList> weightedList;
    private Expression<Number> weight;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.object = LiteralUtils.defendExpression(expressions[0]);
        this.weightedList = (Expression<WeightedList>) expressions[1];
        this.weight = (Expression<Number>) expressions[2];
        return LiteralUtils.canInitSafely(this.object);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(Event event) {
        Object object = this.object.getSingle(event);
        WeightedList list = this.weightedList.getSingle(event);
        Number number = this.weight.getSingle(event);
        if (object == null || list == null || number == null) {
            return;
        }
        int weight = number.intValue();
        if (weight > 0) {
            list.add(object, weight);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add " + this.object.toString(event, debug) + " to " + this.weightedList.toString(event, debug) +
            " with weight " + this.weight.toString(event, debug);
    }

}
