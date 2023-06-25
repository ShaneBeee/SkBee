package com.shanebeestudios.skbee.elements.statistic.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Types {

    static {
        EnumWrapper<Statistic> STATISTICS_ENUM = new EnumWrapper<>(Statistic.class);
        Classes.registerClass(new ClassInfo<>(Statistic.class, "statistic")
                .user("statistics?")
                .name("Statistic")
                .description("Represents the different statistics for a player.",
                        "Some stats require extra data, these are distinguished by their data type within the square brackets.",
                        "Underscores in stat names are not required, you can use spaces.",
                        "NOTE: 'play_one_minute' stat's name is misleading, it's actually amount of ticks played.")
                .usage(getNames())
                .since("1.17.0")
                .parser(STATISTICS_ENUM.getParser()));
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
        for (Statistic statistic : Statistic.values()) {
            String name = statistic.getKey().getKey();
            Type type = statistic.getType();

            if (type == Type.ITEM || type == Type.BLOCK) {
                name = name + " [ItemType]";
            } else if (type == Type.ENTITY) {
                name = name + " [EntityType]";
            }
            names.add(name);
        }
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
