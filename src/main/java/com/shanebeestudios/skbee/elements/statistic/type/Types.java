package com.shanebeestudios.skbee.elements.statistic.type;

import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Types {

    static {
        if (Classes.getExactClassInfo(Statistic.class) == null) {
            Classes.registerClass(RegistryClassInfo.create(Registry.STATISTIC, Statistic.class, false, "statistic")
                .user("statistics?")
                .name("Statistic")
                .description("Represents the different statistics for a player.",
                    "Some stats require extra data, these are distinguished by their data type within the square brackets.",
                    "Underscores in stat names are not required, you can use spaces.",
                    "NOTE: 'play_one_minute' stat's name is misleading, it's actually amount of ticks played.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .usage(getNames())
                .since("1.17.0"));
        }
    }

    /**
     * Get names of all stats
     * <br>
     * This includes the stat type
     *
     * @return List of all stats including type
     */
    private static String getNames() {
        List<String> names = new ArrayList<>();
        Registry.STATISTIC.forEach(statistic -> {
            String name = statistic.getKey().getKey();
            Type type = statistic.getType();

            if (type == Type.ITEM || type == Type.BLOCK) {
                name = name + " [ItemType]";
            } else if (type == Type.ENTITY) {
                name = name + " [EntityType]";
            }
            names.add(name);
        });
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
