package com.shanebeestudios.skbee.elements.fishing;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.fishing.effects.EffFishHookPullIn;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishEventEntity;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishEventState;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishHookHookedEntity;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishHookOfPlayer;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishHookState;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishHookWaitTime;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprFishingExperience;
import com.shanebeestudios.skbee.elements.fishing.expressions.ExprPufferFishState;
import com.shanebeestudios.skbee.elements.fishing.type.Types;

public class FishingElementRegistration {

    public static void register(Registration reg) {
        // EFFECTS
        EffFishHookPullIn.register(reg);

        // EXPRESSIONS
        ExprFishEventEntity.register(reg);
        ExprFishEventState.register(reg);
        ExprFishHookHookedEntity.register(reg);
        ExprFishHookOfPlayer.register(reg);
        ExprFishHookState.register(reg);
        ExprFishHookWaitTime.register(reg);
        ExprFishingExperience.register(reg);
        ExprPufferFishState.register(reg);

        // TYPES
        Types.register(reg);
    }

}
