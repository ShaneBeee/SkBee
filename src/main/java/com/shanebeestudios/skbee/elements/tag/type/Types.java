package com.shanebeestudios.skbee.elements.tag.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import org.bukkit.Tag;

public class Types {

    static {
        if (Classes.getExactClassInfo(Tag.class) == null) {
            Classes.registerClass(new ClassInfo<>(Tag.class, "minecrafttag")
                    .user("minecraft ?tags?")
                    .name("Minecraft Tag")
                    .description("Represents a Minecraft Tag.")
                    .since("INSERT VERSION"));
        }
    }

}
