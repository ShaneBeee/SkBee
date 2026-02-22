package com.shanebeestudios.skbee.elements.worldcreator;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.worldcreator.conditions.CondWorldExists;
import com.shanebeestudios.skbee.elements.worldcreator.effects.EffLoadWorld;
import com.shanebeestudios.skbee.elements.worldcreator.expressions.ExprLoadedCustomWorlds;
import com.shanebeestudios.skbee.elements.worldcreator.expressions.ExprWorldCreator;
import com.shanebeestudios.skbee.elements.worldcreator.expressions.ExprWorldCreatorOption;
import com.shanebeestudios.skbee.elements.worldcreator.type.Types;

public class WorldCreatorElementRegistration {

    public static void register(Registration reg) {
        // CONDTIONS
        CondWorldExists.register(reg);

        // EFFECTS
        EffLoadWorld.register(reg);

        // EXPRESSIONS
        ExprLoadedCustomWorlds.register(reg);
        ExprWorldCreator.register(reg);
        ExprWorldCreatorOption.register(reg);

        // TYPES
        Types.register(reg);
    }

}
