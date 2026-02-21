package com.shanebeestudios.skbee.elements.fishing.type;

import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.entity.FishHook.HookState;

public class Types {

    public static void register(Registration reg) {
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(HookState.class) == null) {
            reg.newEnumType(HookState.class, "fishhookstate")
                .user("fish ?hook ?states?")
                .name("Fish Hook State")
                .since("2.8.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'fishhookstate' already.");
            Util.logLoading("You may have to use their fish hook states in SkBee's fish hook state expression.");
        }
    }

}
