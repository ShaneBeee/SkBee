package com.shanebeestudios.skbee.elements.registry;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeyFrom;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeyValues;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryTagKeys;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryValue;
import com.shanebeestudios.skbee.elements.registry.expression.ExprRegistryValues;
import com.shanebeestudios.skbee.elements.registry.expression.SecExprMusicalInstrument;
import com.shanebeestudios.skbee.elements.registry.type.Types;

public class RegistryElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprRegistryTagKeyFrom.register(reg);
        ExprRegistryTagKeys.register(reg);
        ExprRegistryTagKeyValues.register(reg);
        ExprRegistryValue.register(reg);
        ExprRegistryValues.register(reg);
        SecExprMusicalInstrument.register(reg);

        // TYPES
        Types.register(reg);
    }
}
