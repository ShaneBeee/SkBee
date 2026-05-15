package com.shanebeestudios.skbee.elements.statistic;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.elements.statistic.events.StatisticEvents;
import com.shanebeestudios.skbee.elements.statistic.expressions.ExprPlayerStatistic;
import com.shanebeestudios.skbee.elements.statistic.type.Types;

public class StatisticElementRegistration {

    public static void register(Registration reg) {
        // EVENTS
        StatisticEvents.register(reg);

        // EXPRESSIONS
        ExprPlayerStatistic.register(reg);

        // TYPES
        Types.register(reg);
    }

}
