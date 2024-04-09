package com.shanebeestudios.skbee.elements.generator.type;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class GenEventValues {

    static {
        EventValues.registerEventValue(BiomeGenEvent.class, Location.class, new Getter<>() {
            @Override
            public Location get(BiomeGenEvent event) {
                return event.getLocation();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(HeightGenEvent.class, Location.class, new Getter<>() {
            @Override
            public @Nullable Location get(HeightGenEvent event) {
                return event.getLocation();
            }
        }, EventValues.TIME_NOW);
    }

}
