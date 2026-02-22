package com.shanebeestudios.skbee.elements.tickmanager;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.tickmanager.conditions.CondServerTickEntityFrozen;
import com.shanebeestudios.skbee.elements.tickmanager.conditions.CondServerTickServerFrozen;
import com.shanebeestudios.skbee.elements.tickmanager.conditions.CondServerTickSprintStep;
import com.shanebeestudios.skbee.elements.tickmanager.effects.EffServerTickSprint;
import com.shanebeestudios.skbee.elements.tickmanager.effects.EffServerTickStep;
import com.shanebeestudios.skbee.elements.tickmanager.expressions.ExprServerTickFrozenState;
import com.shanebeestudios.skbee.elements.tickmanager.expressions.ExprServerTickRate;

public class TickManagerElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondServerTickEntityFrozen.register(reg);
        CondServerTickServerFrozen.register(reg);
        CondServerTickSprintStep.register(reg);

        // EFFECTS
        EffServerTickSprint.register(reg);
        EffServerTickStep.register(reg);

        // EXPRESSIONS
        ExprServerTickFrozenState.register(reg);
        ExprServerTickRate.register(reg);
    }

}
