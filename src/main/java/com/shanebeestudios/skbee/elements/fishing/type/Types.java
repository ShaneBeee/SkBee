package com.shanebeestudios.skbee.elements.fishing.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.entity.FishHook.HookState;
import org.bukkit.event.player.PlayerFishEvent;

public class Types {

    static {
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(PlayerFishEvent.State.class) == null) {
            EnumUtils<PlayerFishEvent.State> FISH_STATE_ENUM = new EnumUtils<>(PlayerFishEvent.State.class);
            Classes.registerClass(new ClassInfo<>(PlayerFishEvent.State.class, "fishingstate")
                    .user("fish(ing)? ?states?")
                    .name("Fish Event State")
                    .usage(FISH_STATE_ENUM.getAllNames())
                    .since("1.15.2")
                    .parser(FISH_STATE_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'fishingstate' already.");
            Util.logLoading("You may have to use their fishing states in SkBee's 'Fish Event State' expression.");
        }

        if (Classes.getExactClassInfo(HookState.class) == null) {
            EnumUtils<HookState> FISH_HOOK_STATE_ENUM = new EnumUtils<>(HookState.class);
            Classes.registerClass(new ClassInfo<>(HookState.class, "fishhookstate")
                    .user("fish ?hook ?states?")
                    .name("Fish Hook State")
                    .usage(FISH_HOOK_STATE_ENUM.getAllNames())
                    .since("INSERT VERSION")
                    .parser(FISH_HOOK_STATE_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'fishhookstate' already.");
            Util.logLoading("You may have to use their fish hook states in SkBee's fish hook state expression.");
        }
    }

}
