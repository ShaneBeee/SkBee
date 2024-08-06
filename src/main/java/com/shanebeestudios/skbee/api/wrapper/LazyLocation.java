package com.shanebeestudios.skbee.api.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a lazy version of {@link Location}
 * <br>These are used in {@link com.shanebeestudios.skbee.api.bound.Bound Bounds}
 * when a world might not have been loaded yet
 */
public class LazyLocation extends Location {

    private String worldName;

    public LazyLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        this.worldName = world.getName();
    }

    public LazyLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
        this.worldName = worldName;
    }

    public LazyLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Get the {@link Location} this LazyLocation holds
     * <br>Will attempt to check/update the world
     *
     * @return Location with hopefully updated world
     */
    public Location getLocation() {
        if (super.getWorld() == null) {
            World world = Bukkit.getWorld(this.worldName);
            if (world != null) this.setWorld(world);
        }
        return this;
    }

    /**
     * hidden
     * Copied from {@link Location Bukkit Location}
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();

        if (!data.containsKey("world")) {
            data.put("world", this.worldName);
        }

        return data;
    }

    /**
     * hidden
     * Copied from {@link Location Bukkit Location}
     */
    @NotNull
    public static Location deserialize(@NotNull Map<String, Object> args) {
        World world = null;
        if (args.containsKey("world")) {
            String name = (String) args.get("world");
            world = Bukkit.getWorld(name);
            if (world == null) {
                return new LazyLocation(name, NumberConversions.toDouble(args.get("x")), NumberConversions.toDouble(args.get("y")), NumberConversions.toDouble(args.get("z")), NumberConversions.toFloat(args.get("yaw")), NumberConversions.toFloat(args.get("pitch")));
            }
        }
        return new LazyLocation(world, NumberConversions.toDouble(args.get("x")), NumberConversions.toDouble(args.get("y")), NumberConversions.toDouble(args.get("z")), NumberConversions.toFloat(args.get("yaw")), NumberConversions.toFloat(args.get("pitch")));
    }

}
