package com.shanebeestudios.skbee.elements.other.type;

import com.github.shanebeee.skr.Registration;
import org.bukkit.entity.SpawnCategory;

public class WorldTypes {

    public static void register(Registration reg) {
        reg.newEnumType(SpawnCategory.class, "spawncategory")
            .user("spawn ?categor(y|ies)")
            .name("Spawn Category")
            .description("Represents groups of entities with shared spawn behaviors and mob caps.")
            .since("INSERT VERSION")
            .register();
    }

}
