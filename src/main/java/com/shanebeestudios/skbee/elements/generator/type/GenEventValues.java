package com.shanebeestudios.skbee.elements.generator.type;

import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import org.bukkit.Location;

public class GenEventValues {

    static {
        EventValues.registerEventValue(BiomeGenEvent.class, Location.class, BiomeGenEvent::getLocation, EventValues.TIME_NOW);
        EventValues.registerEventValue(HeightGenEvent.class, Location.class, HeightGenEvent::getLocation, EventValues.TIME_NOW);
    }

}
