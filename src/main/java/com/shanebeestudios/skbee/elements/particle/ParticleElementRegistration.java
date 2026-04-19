package com.shanebeestudios.skbee.elements.particle;

import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.elements.particle.effects.EffParticle;
import com.shanebeestudios.skbee.elements.particle.type.Types;

public class ParticleElementRegistration {

    public static void register(Registration registration) {
        EffParticle.register(registration);
        Types.register(registration);
    }

}
