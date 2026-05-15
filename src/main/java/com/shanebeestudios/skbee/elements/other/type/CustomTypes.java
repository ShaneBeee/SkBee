package com.shanebeestudios.skbee.elements.other.type;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.WeightedList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.properties.Property;
import org.skriptlang.skript.lang.properties.handlers.base.ConditionPropertyHandler;
import org.skriptlang.skript.lang.properties.handlers.base.ExpressionPropertyHandler;

@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class CustomTypes {

    public static void register(Registration reg) {
        reg.newType(WeightedList.class, "weightedlist")
            .user("weighted ?lists?")
            .name("WeightedList")
            .description("A weighted list that allows for random selection based on entry weights.")
            .since("INSERT VERSION")
            .property(Property.SIZE, "The amount of elements in a weighted list.", new WeightedListSize())
            .property(Property.NUMBER, "The amount of elements in a weighted list.", new WeightedListSize())
            .property(Property.AMOUNT, "The amount of elements in a weighted list.", new WeightedListSize())
            .property(Property.IS_EMPTY, "Whether the weighted list is empty.", new WeightedListEmpty())
            .register();
    }

    private static class WeightedListSize implements ExpressionPropertyHandler<WeightedList, Number> {

        @Override
        public Number convert(WeightedList list) {
            return list.size();
        }

        @Override
        public @NotNull Class<Number> returnType() {
            return Number.class;
        }
    }

    private static class WeightedListEmpty implements ConditionPropertyHandler<WeightedList> {

        @Override
        public boolean check(WeightedList propertyHolder) {
            return propertyHolder.isEmpty();
        }
    }

}
