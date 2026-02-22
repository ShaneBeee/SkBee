package com.shanebeestudios.skbee.elements.damagesource;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.damagesource.conditions.CondDamageSourceProperties;
import com.shanebeestudios.skbee.elements.damagesource.effects.EffEntityDamageSource;
import com.shanebeestudios.skbee.elements.damagesource.expressions.ExprDamageSourceCreate;
import com.shanebeestudios.skbee.elements.damagesource.expressions.ExprDamageSourceEvent;
import com.shanebeestudios.skbee.elements.damagesource.expressions.ExprDamageSourceProperties;

public class DamageSourceElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondDamageSourceProperties.register(reg);

        // EFFECTS
        EffEntityDamageSource.register(reg);

        // EXPRESSIONS
        ExprDamageSourceEvent.register(reg);
        ExprDamageSourceCreate.register(reg);
        ExprDamageSourceProperties.register(reg);
    }
}
