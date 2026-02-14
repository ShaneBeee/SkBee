package com.shanebeestudios.skbee.elements.worldcreator.type;

import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.WorldType;

public class Types {

    public static void register(Registration reg) {
        reg.newType(BeeWorldCreator.class, "worldcreator")
            .user("world ?creators?")
            .name("World Creator")
            .description("Used to create new worlds.")
            .examples("set {_creator} to new world creator named \"my-world\"")
            .since("1.8.0")
            .parser(SkriptUtils.getDefaultParser())
            .register();

        if (Classes.getExactClassInfo(WorldType.class) == null) {
            reg.newEnumType(WorldType.class, "worldtype")
                .user("world ?types?")
                .name("World Type")
                .description("The type of a world")
                .examples("set world type of {_creator} to flat")
                .since("1.8.0")
                .register();
        } else {
            Util.log("It looks like another addon registered 'world type' already. ");
            Util.log("You may have to use their world type options in SkBee's 'world creator' system.");
        }
    }

}
