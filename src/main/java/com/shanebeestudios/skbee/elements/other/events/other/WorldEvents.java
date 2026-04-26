package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.GameruleValue;
import com.github.shanebeee.skr.Registration;
import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;

public class WorldEvents extends SimpleEvent {

    public static void register(Registration reg) {
        reg.newEvent(WorldEvents.class, WorldGameRuleChangeEvent.class,
                "world game[ ]rule change")
            .name("World GameRule Change")
            .description("Called when a gamerule is changed in a world.")
            .examples("on world gamerule change:",
                "\tif player is not op:",
                "\t\tcancel event")
            .since("3.21.0")
            .register();

        reg.newEventValue(WorldGameRuleChangeEvent.class, String.class)
            .description("Gets the new value of the gamerule (Why its a string, I dunno!).")
            .converter(WorldGameRuleChangeEvent::getValue)
            .changer(ChangeMode.SET, WorldGameRuleChangeEvent::setValue)
            .patterns("value", "changed-value")
            .register();
        reg.newEventValue(WorldGameRuleChangeEvent.class, GameruleValue.class)
            .description("Gets the new value of the gamerule as a Skript GameRuleValue.")
            .converter(event -> {
                String value = event.getValue();
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return new GameruleValue<>(Boolean.parseBoolean(value));
                } else {
                    return new GameruleValue<>(Integer.parseInt(value));
                }
            })
            .changer(ChangeMode.SET, (event, value) ->
                event.setValue(value.toString()))
            .register();
        reg.newEventValue(WorldGameRuleChangeEvent.class, GameRule.class)
            .description("Gets the gamerule that was changed.")
            .converter(WorldGameRuleChangeEvent::getGameRule)
            .register();
        reg.newEventValue(WorldGameRuleChangeEvent.class, CommandSender.class)
            .description("Gets the command sender associated with this event (null if changed via code).")
            .converter(WorldGameRuleChangeEvent::getCommandSender)
            .register();
    }

}
