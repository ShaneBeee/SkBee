package com.shanebeestudios.skbee.elements.switchcase;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.switchcase.effects.EffCase;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecCase;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecExprSwitchReturn;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecSwitch;

public class SwitchCaseElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffCase.register(reg);

        // SECTIONS
        SecCase.register(reg);
        SecExprSwitchReturn.register(reg);
        SecSwitch.register(reg);
    }

}
