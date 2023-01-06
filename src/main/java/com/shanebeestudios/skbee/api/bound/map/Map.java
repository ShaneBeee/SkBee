package com.shanebeestudios.skbee.api.bound.map;

import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Color;

public interface Map {

    void addMarker(Bound bound);

    void removeMarker(Bound bound);

    void setLabel(Bound bound, String label);

    void setMarkerLineColor(Bound bound, Color color);

    void setMarkerFillColor(Bound bound, Color color);

}
