package com.shanebeestudios.skbee.api.scheduler;

import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TaskUtils {

    public static Scheduler<?> getGlobalScheduler() {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getGlobalScheduler();
        return new SpigotScheduler();
    }

    public static Scheduler<?> getRegionalScheduler(Location location) {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getRegionalScheduler(location);
        return new SpigotScheduler();
    }

    public static Scheduler<?> getEntityScheduler(Entity entity) {
        if (Util.IS_RUNNING_FOLIA) return FoliaScheduler.getEntityScheduler(entity);
        return new SpigotScheduler();
    }

}
