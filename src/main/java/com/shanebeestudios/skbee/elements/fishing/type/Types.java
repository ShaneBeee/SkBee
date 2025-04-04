package com.shanebeestudios.skbee.elements.fishing.type;

import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.entity.FishHook.HookState;
import org.bukkit.event.player.PlayerFishEvent;

public class Types {

    static {
        // Only register if no other addons have registered this class
        if (!Util.IS_RUNNING_SKRIPT_2_11) {
            if (Classes.getExactClassInfo(PlayerFishEvent.State.class) == null) {

                EnumWrapper<PlayerFishEvent.State> FISH_STATE_ENUM = new EnumWrapper<>(PlayerFishEvent.State.class);
                Classes.registerClass(FISH_STATE_ENUM.getClassInfo("fishingstate")
                    .user("fish(ing)? ?states?")
                    .name("Fish Event State")
                    .since("1.15.2"));
            } else {
                Util.logLoading("It looks like another addon registered 'fishingstate' already.");
                Util.logLoading("You may have to use their fishing states in SkBee's 'Fish Event State' expression.");
            }
        }

        if (Classes.getExactClassInfo(HookState.class) == null) {
            EnumWrapper<HookState> FISH_HOOK_STATE_ENUM = new EnumWrapper<>(HookState.class);
            Classes.registerClass(FISH_HOOK_STATE_ENUM.getClassInfo("fishhookstate")
                    .user("fish ?hook ?states?")
                    .name("Fish Hook State")
                    .since("2.8.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'fishhookstate' already.");
            Util.logLoading("You may have to use their fish hook states in SkBee's fish hook state expression.");
        }
    }

}
