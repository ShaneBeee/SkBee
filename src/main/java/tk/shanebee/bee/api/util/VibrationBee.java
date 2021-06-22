package tk.shanebee.bee.api.util;

import org.bukkit.Location;
import org.bukkit.Vibration;
import org.bukkit.Vibration.Destination.BlockDestination;

/**
 * Utility class for MC 1.17's vibrations
 * <p>Versions below 1.17 had issues loading and not being able
 * to find 'org.bukkit.Vibration$Destination' even though this
 * part isn't even loaded. Silly workaround class.</p>
 */
public class VibrationBee {

    Location origin;
    Location destination;
    int arrivalTime;

    public VibrationBee(Location origin, Location destination, int arrivalTime) {
        this.origin = origin;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
    }

    public Vibration get() {
        return new Vibration(origin, new BlockDestination(destination), arrivalTime);
    }

}
