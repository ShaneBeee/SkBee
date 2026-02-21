package com.shanebeestudios.skbee.elements.registry;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeyFrom;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeyValues;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeys;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryValues;
import com.shanebeestudios.skbee.elements.registry.type.Types;

public class RegistryElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprRegistryTagKeyFrom.register(reg);
        ExprRegistryTagKeys.register(reg);
        ExprRegistryTagKeyValues.register(reg);
        ExprRegistryValues.register(reg);

        // TYPES
        Types.register(reg);
    }
}
