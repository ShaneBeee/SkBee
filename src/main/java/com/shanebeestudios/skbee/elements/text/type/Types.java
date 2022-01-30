package com.shanebeestudios.skbee.elements.text.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BaseComponent.class, "basecomponent")
                .user("base ?components?")
                .name("Text Component - Base Component")
                .description("Text components used for hover/click events. Due to the complexity of these, ",
                        "they can NOT be long term stored in variables.")
                .examples("set {_t} to text component from \"CLICK FOR OUR DISCORD\"",
                        "set hover event of {_t} to a new hover event showing \"Clicky Clicky!\"",
                        "set click event of {_t} to a new click event to open url \"https://OurDiscord.com\"",
                        "send component {_t} to player")
                .since("1.5.0")
                .parser(new Parser<BaseComponent>() {
                    @Override
                    public @NotNull String toString(@NotNull BaseComponent o, int flags) {
                        return o.toLegacyText();
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toVariableNameString(@NotNull BaseComponent o) {
                        return o.toLegacyText();
                    }

                    public @NotNull String getVariableNamePattern() {
                        return "";
                    }
                }));
    }

}
