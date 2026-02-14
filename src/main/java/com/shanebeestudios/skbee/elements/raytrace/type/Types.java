package com.shanebeestudios.skbee.elements.raytrace.type;

import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.util.RayTraceResult;

public class Types {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(RayTraceResult.class) == null) {
            reg.newType(RayTraceResult.class, "raytraceresult")
                .user("ray ?trace ?results?")
                .name("RayTrace - Result")
                .description("The hit result of a ray trace.",
                    "Only the hit position is guaranteed to always be available.",
                    "The availability of the other attributes depends on what got hit",
                    "and on the context in which the ray trace was performed.")
                .since("2.6.0")
                .parser(SkriptUtils.getDefaultParser())
                .register();
        }
    }

}
