package com.shanebeestudios.skbee.elements.statistic.events;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;

public class StatisticEvents extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(StatisticEvents.class, PlayerStatisticIncrementEvent.class,
                "player statistic increment")
            .name("Statistic - Increment")
            .description("Called when a player statistic is incremented.")
            .since("INSERT VERSION")
            .register();

        reg.newEventValue(PlayerStatisticIncrementEvent.class, Statistic.class)
            .description("Gets the statistic that is being incremented.")
            .converter(PlayerStatisticIncrementEvent::getStatistic)
            .register();
        reg.newEventValue(PlayerStatisticIncrementEvent.class, Number.class)
            .description("Gets the previous value of the statistic.")
            .patterns("value")
            .time(EventValue.Time.PAST)
            .converter(PlayerStatisticIncrementEvent::getPreviousValue)
            .register();
        reg.newEventValue(PlayerStatisticIncrementEvent.class, Number.class)
            .description("Gets the new value of the statistic.")
            .patterns("value")
            .converter(PlayerStatisticIncrementEvent::getNewValue)
            .register();
        reg.newEventValue(PlayerStatisticIncrementEvent.class, ItemType.class)
            .description("Gets the Material if the statistic is a block or item statistic otherwise returns null.")
            .converter(event -> {
                Material material = event.getMaterial();
                if (material != null) {
                    return new ItemType(material);
                }
                return null;
            })
            .register();
        reg.newEventValue(PlayerStatisticIncrementEvent.class, EntityType.class)
            .description("Gets the EntityType if the statistic is an entity statistic otherwise returns null.")
            .converter(PlayerStatisticIncrementEvent::getEntityType)
            .register();
        reg.newEventValue(PlayerStatisticIncrementEvent.class, EntityData.class)
            .description("Gets the EntityData if the statistic is an entity statistic otherwise returns null.")
            .converter(event -> {
                EntityType entityType = event.getEntityType();
                if (entityType != null) {
                    return EntityUtils.toSkriptEntityData(entityType);
                }
                return null;
            })
            .register();
    }

}
