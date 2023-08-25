package com.shanebeestudios.skbee.elements.objective.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(Objective.class, "objective")
                .user("objectives?")
                .name("Scoreboard - Objective")
                .description("Represents an objective in a scoreboard.",
                        "When deleting, the objective will be unregistered.")
                .since("2.6.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Objective o, int flags) {
                        return "objective with id \"" + o.getName() + "\"";
                    }

                    @Override
                    public @NotNull String toVariableNameString(Objective o) {
                        return "objective{name=" + o.getName() + "}";
                    }
                })
                .changer(new Changer<>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            return CollectionUtils.array(Objective.class);
                        }
                        return null;
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void change(Objective[] what, @Nullable Object[] delta, ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) {
                            for (Objective objective : what) {
                                objective.unregister();
                            }
                        }
                    }
                }));

        if (Skript.classExists("org.bukkit.scoreboard.Criteria")) {
            Classes.registerClass(new ClassInfo<>(Criteria.class, "criteria")
                    .user("criterias?")
                    .name("Scoreboard - Criteria")
                    .description("Represents a criteria for a scoreboard objective.",
                            "More info: <link>https://minecraft.fandom.com/wiki/Scoreboard#Criteria</link>")
                    .since("2.6.0")
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(@NotNull ParseContext context) {
                            return false;
                        }

                        @Override
                        public @NotNull String toString(Criteria o, int flags) {
                            return "criteria " + o.getName();
                        }

                        @Override
                        public @NotNull String toVariableNameString(Criteria o) {
                            return "criteria{name=" + o.getName() + "}";
                        }
                    }));
        }

        EnumWrapper<RenderType> RENDER_ENUM = new EnumWrapper<>(RenderType.class);
        Classes.registerClass(RENDER_ENUM.getClassInfo("rendertype")
                .user("render ?types?")
                .name("Scoreboard - Objective Render Type")
                .description("Controls the way in which an Objective is rendered client side.")
                .since("2.6.0"));

        EnumWrapper<DisplaySlot> DISPLAY_ENUM = new EnumWrapper<>(DisplaySlot.class);
        Classes.registerClass(DISPLAY_ENUM.getClassInfo("displayslot")
                .user("display ?slots?")
                .name("Scoreboard - Objective Display Slot")
                .description("Locations for displaying objectives to the player")
                .since("2.6.0"));
    }

}
