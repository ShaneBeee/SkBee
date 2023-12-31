package com.shanebeestudios.skbee.elements.tag.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

public class Types {

    public static boolean HAS_TAG;

    static {
        if (Classes.getExactClassInfo(Tag.class) == null) {
            HAS_TAG = true;
            Classes.registerClass(new ClassInfo<>(Tag.class, "minecrafttag")
                    .user("minecraft ?tags?")
                    .name("Minecraft Tag")
                    .description("Represents a Minecraft Tag.")
                    .since("2.6.0")
                    .parser(new Parser<>() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public @NotNull String toString(Tag tag, int flags) {
                            return tag.getKey().toString();
                        }

                        @Override
                        public @NotNull String toVariableNameString(Tag tag) {
                            return toString(tag, 0);
                        }
                    }));
        } else {
            Util.logLoading("It looks like another addon registered 'minecraft tag' already.");
            Util.logLoading("You may have to use their Minecraft Tags in SkBee's Minecraft Tag expressions.");
        }
    }

}
