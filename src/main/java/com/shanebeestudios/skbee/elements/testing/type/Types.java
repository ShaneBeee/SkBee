package com.shanebeestudios.skbee.elements.testing.type;

import ch.njol.skript.registrations.Classes;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.variables.FlatFileStorage;
import ch.njol.skript.variables.Variables;
import ch.njol.skript.variables.VariablesStorage;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.Bukkit;
import org.bukkit.Registry;

import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    static {
        Classes.registerClass(RegistryClassInfo.create(Registry.DATA_COMPONENT_TYPE, DataComponentType.class,
                false, "datacomponenttype")
            .user("data ?component ?types?")
            .name("Data Component Type"));

        if (TestMode.DEV_MODE) {
            // If running dev mode, block variables starting with "test_" from saving to file
            // This helps keep the variable file to a minimum when testing on a normal server
            Bukkit.getScheduler().runTaskLater(SkBee.getPlugin(), () -> {
                for (VariablesStorage store : Variables.getStores()) {
                    if (store instanceof FlatFileStorage flat) {
                        Util.log("Setting up variable pattern for '" + flat.getClass().getSimpleName() + "'");
                        ReflectionUtils.setField("variableNamePattern", VariablesStorage.class, flat, Pattern.compile("(?!test_).*"));
                        Util.log("Pattern set to ignore variables starting with \"test_\"");
                    }
                }
            }, 1);
        }
    }

}
