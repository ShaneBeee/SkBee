package com.shanebeestudios.skbee.elements.fastboard;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.fastboard.conditions.CondFastBoardOn;
import com.shanebeestudios.skbee.elements.fastboard.effects.EffFastBoardClear;
import com.shanebeestudios.skbee.elements.fastboard.effects.EffFastBoardToggle;
import com.shanebeestudios.skbee.elements.fastboard.expressions.ExprFastBoardLine;
import com.shanebeestudios.skbee.elements.fastboard.expressions.ExprFastBoardTitle;

public class FastboardElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondFastBoardOn.register(reg);

        // EFFECTS
        EffFastBoardClear.register(reg);
        EffFastBoardToggle.register(reg);

        // EXPRESSIONS
        ExprFastBoardLine.register(reg);
        ExprFastBoardTitle.register(reg);
    }

}
