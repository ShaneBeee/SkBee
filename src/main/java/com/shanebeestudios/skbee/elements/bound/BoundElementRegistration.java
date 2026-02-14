package com.shanebeestudios.skbee.elements.bound;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.bound.conditions.CondBoundContainsLocation;
import com.shanebeestudios.skbee.elements.bound.conditions.CondBoundIntersects;
import com.shanebeestudios.skbee.elements.bound.conditions.CondBoundIsTemporary;
import com.shanebeestudios.skbee.elements.bound.effects.EffBoundResize;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundBlocks;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundCoords;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundEntities;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundFromID;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundFullState;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundID;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundLocations;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundOwnerMember;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundValue;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundWorld;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundsAll;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprBoundsAtLocation;
import com.shanebeestudios.skbee.elements.bound.expressions.ExprLastCreatedBound;
import com.shanebeestudios.skbee.elements.bound.sections.EffSecBoundCopy;
import com.shanebeestudios.skbee.elements.bound.sections.EffSecBoundCreate;
import com.shanebeestudios.skbee.elements.bound.types.SkriptTypes;

public class BoundElementRegistration {

    public static void register(Registration reg) {
        // SECTIONS
        EffSecBoundCreate.register(reg);
        EffSecBoundCopy.register(reg);

        // CONDITIONS
        CondBoundContainsLocation.register(reg);
        CondBoundIntersects.register(reg);
        CondBoundIsTemporary.register(reg);

        // EFFECTS
        EffBoundResize.register(reg);

        // EXPRESSIONS
        ExprBoundBlocks.register(reg);
        ExprBoundCoords.register(reg);
        ExprBoundEntities.register(reg);
        ExprBoundFromID.register(reg);
        ExprBoundFullState.register(reg);
        ExprBoundID.register(reg);
        ExprBoundLocations.register(reg);
        ExprBoundOwnerMember.register(reg);
        ExprBoundValue.register(reg);
        ExprBoundWorld.register(reg);
        ExprBoundsAll.register(reg);
        ExprBoundsAtLocation.register(reg);
        ExprLastCreatedBound.register(reg);

        // TYPES
        SkriptTypes.register(reg);
    }

}
