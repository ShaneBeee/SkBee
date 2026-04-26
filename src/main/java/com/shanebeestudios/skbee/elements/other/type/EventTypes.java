package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.registrations.Classes;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

public class EventTypes {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(EntityKnockbackEvent.Cause.class) == null) {
            reg.newEnumType(EntityKnockbackEvent.Cause.class, "knockbackcause")
                .user("knockback ?causes?")
                .defaultExpression(new EventValueExpression<>(EntityKnockbackEvent.Cause.class))
                .name("Entity Knockback Cause")
                .description("Represents the cause of knockback in an entity knockback event")
                .since("3.16.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'knockbackcause' already.");
            Util.logLoading("You may have to use their KnockbackCause in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(EntityRemoveEvent.Cause.class) == null) {
            reg.newEnumType(EntityRemoveEvent.Cause.class, "entityremovecause")
                .user("entity ?remove ?causes?")
                .name("Entity Remove Cause")
                .description("Represents the reasons an entity was removed from the world.", Util.AUTO_GEN_NOTE)
                .after("damagecause", "damagetype")
                .since("3.4.0")
                .register();
        }

        if (Classes.getExactClassInfo(PlayerFailMoveEvent.FailReason.class) == null) {
            reg.newEnumType(PlayerFailMoveEvent.FailReason.class, "failmovereason")
                .name("Fail Reason")
                .user("fail ?move ?reasons?")
                .description("The reason a player failed to move in a `player fail move` event.")
                .since("3.11.0")
                .register();
        }

        if (Classes.getExactClassInfo(PlayerSetSpawnEvent.Cause.class) == null) {
            reg.newEnumType(PlayerSetSpawnEvent.Cause.class, "playerspawnchangereason")
                .user("player ?spawn ?change ?reasons?")
                .name("Player Spawn Change Reason")
                .description("Represents the reasons why a player changed their spawn location.", Util.AUTO_GEN_NOTE)
                .after("damagecause", "damagetype", "itemtype")
                .since("3.4.0")
                .register();
        }
    }

}
