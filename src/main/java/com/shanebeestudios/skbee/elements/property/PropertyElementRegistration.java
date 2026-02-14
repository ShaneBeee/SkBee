package com.shanebeestudios.skbee.elements.property;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.property.expressions.ExprProperty;
import com.shanebeestudios.skbee.elements.property.properties.EntityProperties;
import com.shanebeestudios.skbee.elements.property.properties.ItemProperties;
import com.shanebeestudios.skbee.elements.property.properties.PlayerProperties;
import com.shanebeestudios.skbee.elements.property.type.Type;

public class PropertyElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprProperty.register(reg);

        // PROPERTIES
        EntityProperties.register(reg);
        ItemProperties.register(reg);
        PlayerProperties.register(reg);

        // TYPES
        Type.register(reg);
    }
}
