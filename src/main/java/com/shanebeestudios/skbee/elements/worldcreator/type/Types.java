package com.shanebeestudios.skbee.elements.worldcreator.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BeeWorldCreator.class, "worldcreator")
                .user("world ?creators?")
                .name("World Creator")
                .description("Used to create new worlds.")
                .examples("set {_creator} to new world creator named \"my-world\"")
                .since("1.8.0"));

        if (Classes.getExactClassInfo(Environment.class) == null) {
            EnumWrapper<Environment> environments = new EnumWrapper<>(Environment.class);
            Classes.registerClass(new ClassInfo<>(Environment.class, "environment")
                    .user("environments?")
                    .name("Environment")
                    .description("The environment of a world.")
                    .usage(environments.getAllNames())
                    .examples("set environment of {_creator} to nether")
                    .since("1.8.0")
                    .parser(environments.getParser()));
        } else {
            Util.log("It looks like another addon registered 'environment' already.");
            Util.log("You may have to use their environment options in SkBee's 'world creator' system.");
        }

        if (Classes.getExactClassInfo(WorldType.class) == null) {
            EnumWrapper<WorldType> worldTypes = new EnumWrapper<>(WorldType.class);
            Classes.registerClass(new ClassInfo<>(WorldType.class, "worldtype")
                    .user("world ?types?")
                    .name("World Type")
                    .description("The type of a world")
                    .usage(worldTypes.getAllNames())
                    .examples("set world type of {_creator} to flat")
                    .since("1.8.0")
                    .parser(worldTypes.getParser()));
        } else {
            Util.log("It looks like another addon registered 'world type' already. ");
            Util.log("You may have to use their world type options in SkBee's 'world creator' system.");
        }
    }

}
