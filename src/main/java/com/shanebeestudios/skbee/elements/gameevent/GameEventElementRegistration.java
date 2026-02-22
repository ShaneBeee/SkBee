package com.shanebeestudios.skbee.elements.gameevent;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.gameevent.events.EvtGameEvents;
import com.shanebeestudios.skbee.elements.gameevent.expressions.ExprGameEventRadius;
import com.shanebeestudios.skbee.elements.gameevent.type.Types;

public class GameEventElementRegistration {

    public static void register(Registration reg) {
        // EVENTS
        EvtGameEvents.register(reg);

        // EXPRESSIONS
        ExprGameEventRadius.register(reg);

        // TYPES
        Types.register(reg);
    }

}
