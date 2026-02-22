package com.shanebeestudios.skbee.elements.structure;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.structure.conditions.CondStructureExists;
import com.shanebeestudios.skbee.elements.structure.effects.EffStructureFill;
import com.shanebeestudios.skbee.elements.structure.effects.EffStructurePlace;
import com.shanebeestudios.skbee.elements.structure.effects.EffStructureSave;
import com.shanebeestudios.skbee.elements.structure.expressions.ExprStructureBlockStates;
import com.shanebeestudios.skbee.elements.structure.expressions.ExprStructureLastPlacedLocation;
import com.shanebeestudios.skbee.elements.structure.expressions.ExprStructureObject;
import com.shanebeestudios.skbee.elements.structure.expressions.ExprStructureProperties;
import com.shanebeestudios.skbee.elements.structure.type.Types;

public class StructureElementRegistration {

    public static void register(Registration reg) {
        // CONDITIONS
        CondStructureExists.register(reg);

        // EFFECTS
        EffStructureFill.register(reg);
        EffStructurePlace.register(reg);
        EffStructureSave.register(reg);

        // EXPRESSIONS
        ExprStructureBlockStates.register(reg);
        ExprStructureLastPlacedLocation.register(reg);
        ExprStructureObject.register(reg);
        ExprStructureProperties.register(reg);

        // TYPES
        Types.register(reg);
    }

}
