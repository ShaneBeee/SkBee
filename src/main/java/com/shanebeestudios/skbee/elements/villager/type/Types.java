package com.shanebeestudios.skbee.elements.villager.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.entity.Villager;

public class Types {

    static {
        // VILLAGER PROFESSION
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Villager.Profession.class) == null) {
            EnumUtils<Villager.Profession> VILLAGER_PROFESSION_ENUM = new EnumUtils<>(Villager.Profession.class, "", "profession");
            Classes.registerClass(new ClassInfo<>(Villager.Profession.class, "profession")
                    .user("professions?")
                    .name("Villager Profession")
                    .description("Represent the types of professions for villagers.",
                            "Due to not parsing correctly, the professions are suffixed with 'profession'.")
                    .usage(VILLAGER_PROFESSION_ENUM.getAllNames())
                    .since("INSERT VERSION")
                    .parser(VILLAGER_PROFESSION_ENUM.getParser()));
        }

        // VILLAGER TYPE
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Villager.Type.class) == null) {
            EnumUtils<Villager.Type> VILLAGER_TYPE_ENUM = new EnumUtils<>(Villager.Type.class, "", "villager");
            Classes.registerClass(new ClassInfo<>(Villager.Type.class, "villagertype")
                    .user("villager ?types?")
                    .name("Villager Type")
                    .description("Represents the types of villagers.",
                            "Due to possible overlaps with biomes, types are suffixed with 'villager'.")
                    .usage(VILLAGER_TYPE_ENUM.getAllNames())
                    .since("INSERT VERSION")
                    .parser(VILLAGER_TYPE_ENUM.getParser()));
        }
    }

}
