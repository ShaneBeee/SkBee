package com.shanebeestudios.skbee.elements.statistic;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.statistic.expressions.ExprPlayerStatistic;
import com.shanebeestudios.skbee.elements.statistic.type.Types;

public class StatisticElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprPlayerStatistic.register(reg);

        // TYPES
        Types.register(reg);
    }

}
