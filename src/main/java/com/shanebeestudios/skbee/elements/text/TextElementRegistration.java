package com.shanebeestudios.skbee.elements.text;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.text.events.EvtChat;
import com.shanebeestudios.skbee.elements.text.type.Types;

public class TextElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS

        // EVENTS
        EvtChat.register(reg);

        // EXPRESSIONS

        // SECTIONS

        // TYPES
        Types.register(reg);
    }
}
