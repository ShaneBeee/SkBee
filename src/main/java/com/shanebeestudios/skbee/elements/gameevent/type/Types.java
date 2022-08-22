package com.shanebeestudios.skbee.elements.gameevent.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(GameEvent.class, "gameevent")
                .name("Game Event")
                .user("game ?events?")
                .description("Represents a Minecraft 'GameEvent', mainly used by Skulk Sensors. Requires MC 1.17+.",
                        "See McWiki for more details -> https://minecraft.fandom.com/wiki/Sculk_Sensor#Vibration_amplitudes")
                .usage(getGameEventNames())
                .examples("")
                .since("1.14.0")
                .parser(new Parser<GameEvent>() {

                    @SuppressWarnings("NullableProblems")
                    @Nullable
                    @Override
                    public GameEvent parse(String string, ParseContext context) {
                        try {
                            NamespacedKey namespacedKey = Util.getNamespacedKey(string, false);
                            if (namespacedKey == null) return null;
                            return GameEvent.getByKey(namespacedKey);
                        } catch (IllegalArgumentException ignore) {
                            return null;
                        }
                    }

                    @Override
                    public @NotNull String toString(GameEvent gameEvent, int flags) {
                        return gameEvent.getKey().getKey();
                    }

                    @Override
                    public @NotNull String toVariableNameString(GameEvent gameEvent) {
                        return "gameevent:" + gameEvent.getKey().getKey();
                    }

                    public String getVariableNamePattern() {
                        return "gameevent://s";
                    }
                }));
    }

    private static String getGameEventNames() {
        List<String> names = new ArrayList<>();
        for (GameEvent value : GameEvent.values()) {
            names.add(value.getKey().getKey());
        }
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

}
