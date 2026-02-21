package com.shanebeestudios.skbee.elements.testing.type;

import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.variables.FlatFileStorage;
import ch.njol.skript.variables.Variables;
import ch.njol.skript.variables.VariablesStorage;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;

import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    public static void register(Registration reg) {
        if (TestMode.DEV_MODE) {
            // If running dev mode, block variables starting with "test_" from saving to file
            // This helps keep the variable file to a minimum when testing on a normal server
            TaskUtils.getGlobalScheduler().runTaskLater(() -> {
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
