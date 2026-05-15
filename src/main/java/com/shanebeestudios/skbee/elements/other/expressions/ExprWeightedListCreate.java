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
public class ExprWeightedListCreate extends SimpleExpression<WeightedList> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprWeightedListCreate.class, WeightedList.class,
                "[a] new weighted[ ]list")
            .name("WeightedList - Create")
            .description("Creates a new weighted list for use with other WeightedList expressions.")
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

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected WeightedList<?> @Nullable [] get(Event event) {
        return new WeightedList[]{new WeightedList<>()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WeightedList> getReturnType() {
        return WeightedList.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new weighted list";
    }

}
