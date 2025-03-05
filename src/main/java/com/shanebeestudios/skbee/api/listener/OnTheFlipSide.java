package com.shanebeestudios.skbee.api.listener;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.wrapper.PDCWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Calendar;

/**
 * A Monumental breakthru in flip side entity technology
 */
public class OnTheFlipSide implements Listener {

    private static final boolean FLIP_SIDE = SkBee.getPlugin().getPluginConfig().on_the_flip_side;

    @SuppressWarnings("deprecation")
    @EventHandler
    private void onEntityEnterTheLabyrinth(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob)) return;

        PDCWrapper pdc = PDCWrapper.wrap(entity);
        if (FLIP_SIDE && isTheDayOfTotalFlipSidiness()) {
            if (entity.getCustomName() == null) {
                entity.setCustomName("Grumm");
                pdc.setBoolean("flip_side", true);
            }
        } else if (entity.getCustomName() != null && pdc.hasKey("flip_side")) {
            entity.setCustomName(null);
            pdc.deleteKey("flip_side");
        }
    }

    private boolean isTheDayOfTotalFlipSidiness() {
        Calendar instance = Calendar.getInstance();
        if (instance.get(Calendar.MONTH) == Calendar.APRIL && instance.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

}
