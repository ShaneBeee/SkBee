package com.shanebeestudios.skbee.elements.worldborder.type;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.Nullable;

public class Types {

    private static final Changer<WorldBorder> BORDER_CHANGER = new Changer<>() {
        @SuppressWarnings("NullableProblems")
        @Override
        public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
            if (mode == ChangeMode.RESET) return CollectionUtils.array(WorldBorder.class);
            return null;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public void change(WorldBorder[] worldBorders, @Nullable Object[] objects, ChangeMode mode) {
            if (mode == ChangeMode.RESET) {
                for (WorldBorder worldBorder : worldBorders) {
                    worldBorder.reset();
                }
            }
        }
    };

    static {
        if (Classes.getExactClassInfo(WorldBorder.class) == null) {
            Classes.registerClass(new ClassInfo<>(WorldBorder.class, "worldborder")
                    .user("world ?borders?")
                    .name("World Border")
                    .description("Represents the world border of a world/player.",
                            "World borders can be reset.",
                            "(This will need to be done from a var since the expression handles it differently)")
                    .examples("set {_w} to world border of player",
                            "reset {_w}")
                    .since("1.17.0")
                    .parser(new Parser<>() {
                        @SuppressWarnings("NullableProblems")
                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public String toString(WorldBorder worldBorder, int i) {
                            World world = worldBorder.getWorld();
                            if (world != null) {
                                String worldName = Classes.toString(world);
                                return "world border of world '" + worldName + "'";
                            }
                            String borderString = worldBorder.toString();
                            if (borderString.contains("@")) {
                                borderString = " @" + borderString.split("@")[1];
                            } else {
                                borderString = "";
                            }
                            return "virtual world border" + borderString;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public String toVariableNameString(WorldBorder worldBorder) {
                            return "worldborder: " + toString(worldBorder, 0);
                        }
                    })
                    .changer(BORDER_CHANGER));
        } else {
            Util.logLoading("It looks like another addon registered 'worldborder' already.");
            Util.logLoading("You may have to use their worldborder in SkBee's 'WorldBorder elements.");
        }
    }
}
