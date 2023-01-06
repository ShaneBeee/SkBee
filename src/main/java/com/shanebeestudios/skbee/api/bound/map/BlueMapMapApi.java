package com.shanebeestudios.skbee.api.bound.map;

import com.flowpowered.math.vector.Vector2d;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.Marker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Shape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class BlueMapMapApi implements MapApi {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Optional<BlueMapAPI> BLUE_API = BlueMapAPI.getInstance();

    private static Optional<BlueMapAPI> getBlueApi() {
        if (BLUE_API.isPresent()) {
            return BLUE_API;
        }
        BLUE_API = BlueMapAPI.getInstance();
        return BLUE_API;
    }

    List<Bound> delayedBounds = new ArrayList<>();

    BlueMapMapApi() {
        // If a bound loads before BlueMap, we will delay the markers
        BlueMapAPI.onEnable(api -> {
            for (Bound delayedBound : delayedBounds) {
                addMarker(api, delayedBound);
            }
        });
        delayedBounds.clear();
    }

    @Override
    public void addMarker(Bound bound) {
        getBlueApi().ifPresentOrElse(api -> addMarker(api, bound),
                () -> delayedBounds.add(bound));
    }

    @Override
    public void removeMarker(Bound bound) {
        getMarkerSet(bound.getWorld()).getMarkers().remove("bound-" + bound.getId());
        getBlueApi().ifPresent(api -> updateMarkerSet(bound.getWorld(), api));
    }

    @Override
    public void setLabel(Bound bound, String label) {
        ExtrudeMarker marker = getMarker(bound);
        marker.setLabel(label);
    }

    @Override
    public void setMarkerLineColor(Bound bound, Color color) {
        ExtrudeMarker marker = getMarker(bound);
        marker.setLineColor(getBlueMapColor(color, 1.0f));
    }

    @Override
    public void setMarkerFillColor(Bound bound, Color color) {
        ExtrudeMarker marker = getMarker(bound);
        marker.setFillColor(getBlueMapColor(color, 0.3f));
    }

    private void addMarker(BlueMapAPI api, Bound bound) {
        String id = bound.getId();
        World world = bound.getWorld();
        Location lesser = bound.getLesserCorner();
        Location greater = bound.getGreaterCorner();
        Vector2d[] vec = new Vector2d[4];
        vec[0] = new Vector2d(lesser.getX(), lesser.getZ());
        vec[1] = new Vector2d(lesser.getX(), greater.getZ());
        vec[2] = new Vector2d(greater.getX(), greater.getZ());
        vec[3] = new Vector2d(greater.getX(), lesser.getZ());
        Shape shape = new Shape(vec);

        String label = bound.getLabel();
        ExtrudeMarker marker = new ExtrudeMarker(label, shape, (float) lesser.getY(), (float) greater.getY());

        marker.setLineColor(getBlueMapColor(bound.getLineColor(), 1.0f));
        marker.setFillColor(getBlueMapColor(bound.getFillColor(), 0.3f));
        getMarkerSet(world).getMarkers().put("bound-" + id, marker);
        updateMarkerSet(world, api);
    }

    private void updateMarkerSet(World world, BlueMapAPI api) {
        api.getWorld(world).ifPresent(blueMapWorld -> {
            MarkerSet markerSet = getMarkerSet(world);
            for (de.bluecolored.bluemap.api.BlueMapMap map : blueMapWorld.getMaps()) {
                map.getMarkerSets().put("skbee-bound-set", markerSet);
            }
        });
    }

    private de.bluecolored.bluemap.api.math.Color getBlueMapColor(Color color, float alpha) {
        return new de.bluecolored.bluemap.api.math.Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private ExtrudeMarker getMarker(Bound bound) {
        Marker marker = getMarkerSet(bound.getWorld()).getMarkers().get("bound-" + bound.getId());
        return (ExtrudeMarker) marker;
    }

    private final Map<String, MarkerSet> markerSets = new HashMap<>();

    private MarkerSet getMarkerSet(@NotNull World world) {
        String worldName = world.getName();
        if (markerSets.containsKey(worldName)) {
            return markerSets.get(worldName);
        }
        MarkerSet markerSet = MarkerSet.builder().label(BoundConfig.MARKER_SET_LABEL).build();
        markerSets.put(worldName, markerSet);
        return markerSet;
    }

}
