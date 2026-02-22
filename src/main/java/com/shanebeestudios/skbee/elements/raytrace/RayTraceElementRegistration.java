package com.shanebeestudios.skbee.elements.raytrace;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceFromEntity;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceFromLocation;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceHitBlock;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceHitBlockFace;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceHitEntity;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceHitLocation;
import com.shanebeestudios.skbee.elements.raytrace.expressions.ExprRayTraceHitPosition;
import com.shanebeestudios.skbee.elements.raytrace.type.Types;

public class RayTraceElementRegistration {

    public static void register(Registration reg) {
        // EXPRESSIONS
        ExprRayTraceFromEntity.register(reg);
        ExprRayTraceFromLocation.register(reg);
        ExprRayTraceHitBlock.register(reg);
        ExprRayTraceHitBlockFace.register(reg);
        ExprRayTraceHitEntity.register(reg);
        ExprRayTraceHitLocation.register(reg);
        ExprRayTraceHitPosition.register(reg);

        // TYPES
        Types.register(reg);
    }

}
