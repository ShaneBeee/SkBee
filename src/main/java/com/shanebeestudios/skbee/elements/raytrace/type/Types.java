package com.shanebeestudios.skbee.elements.raytrace.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import org.bukkit.util.RayTraceResult;

public class Types {

    static {
        if (Classes.getExactClassInfo(RayTraceResult.class) == null) {
            Classes.registerClass(new ClassInfo<>(RayTraceResult.class, "raytraceresult")
                    .user("ray ?trace ?results?")
                    .name("RayTrace - Result")
                    .description("The hit result of a ray trace.",
                            "Only the hit position is guaranteed to always be available.",
                            "The availability of the other attributes depends on what got hit",
                            "and on the context in which the ray trace was performed.")
                    .since("2.6.0"));
        }
    }

}
