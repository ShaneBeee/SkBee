package com.shanebeestudios.skbee.elements.gameevent.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation") // GameEvent.values() <-- paper did this I'm assuming
public class Types {

    private static final Map<String, GameEvent> GAME_EVENT_MAP = new HashMap<>();

    static {
        List<String> names = new ArrayList<>();
        for (GameEvent gameEvent : GameEvent.values()) {
            NamespacedKey key = gameEvent.getKey();
            GAME_EVENT_MAP.put(key.toString(), gameEvent);
            GAME_EVENT_MAP.put(key.getKey(), gameEvent);
            names.add(key.getKey());
        }
        Collections.sort(names);
        String namesString = StringUtils.join(names, ", ");

        Classes.registerClass(new ClassInfo<>(GameEvent.class, "gameevent")
                .name("Game Event")
                .user("game ?events?")
                .description("Represents a Minecraft 'GameEvent', mainly used by Skulk Sensors. Requires MC 1.17+.",
                        "See McWiki for more details -> https://minecraft.wiki/w/Sculk_Sensor#Vibration_amplitudes")
                .usage(namesString)
                .after("itemtype")
                .examples("")
                .since("1.14.0")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Nullable
                    @Override
                    public GameEvent parse(String string, ParseContext context) {
                        if (string.contains(" "))
                            string = string.replace(" ", "_");
                        if (GAME_EVENT_MAP.containsKey(string)) return GAME_EVENT_MAP.get(string);
                        return null;
                    }

                    @Override
                    public @NotNull String toString(GameEvent gameEvent, int flags) {
                        return gameEvent.getKey().getKey();
                    }

                    @Override
                    public @NotNull String toVariableNameString(GameEvent gameEvent) {
                        return "gameevent:" + gameEvent.getKey().getKey();
                    }
                }));
    }

}
