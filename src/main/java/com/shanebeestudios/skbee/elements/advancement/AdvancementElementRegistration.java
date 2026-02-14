package com.shanebeestudios.skbee.elements.advancement;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.advancement.conditions.CondAdvancementDone;
import com.shanebeestudios.skbee.elements.advancement.effects.EffAdvancementCriteriaAward;
import com.shanebeestudios.skbee.elements.advancement.effects.EffAdvancementLoad;
import com.shanebeestudios.skbee.elements.advancement.event.SimpleEvents;
import com.shanebeestudios.skbee.elements.advancement.expressions.ExprAdvancementAll;
import com.shanebeestudios.skbee.elements.advancement.expressions.ExprAdvancementCriteria;
import com.shanebeestudios.skbee.elements.advancement.expressions.ExprAdvancementProgress;
import com.shanebeestudios.skbee.elements.advancement.expressions.ExprAdvancementProgressAwarded;
import com.shanebeestudios.skbee.elements.advancement.type.Types;

public class AdvancementElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondAdvancementDone.register(reg);

        // EFFECTS
        EffAdvancementCriteriaAward.register(reg);
        EffAdvancementLoad.register(reg);

        // EVENTS
        SimpleEvents.register(reg);

        // EXPRESSIONS
        ExprAdvancementAll.register(reg);
        ExprAdvancementCriteria.register(reg);
        ExprAdvancementProgress.register(reg);
        ExprAdvancementProgressAwarded.register(reg);

        // TYPE
        Types.register(reg);
    }
}
