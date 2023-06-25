package com.shanebeestudios.skbee.elements.text.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Converters;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.jetbrains.annotations.NotNull;

public class Types {

    static {
        // Allow components to be used anywhere a string can
        Converters.registerConverter(ComponentWrapper.class, String.class, ComponentWrapper::toString);

        Classes.registerClass(new ClassInfo<>(ComponentWrapper.class, "textcomponent")
                .user("text ?components?")
                .name("Text Component - Text Component")
                .description("Text components used for hover/click events. Due to the complexity of these, ",
                        "they can NOT be long term stored in variables. \n\bRequires a PaperMC server.")
                .examples("set {_t} to text component from \"CLICK FOR OUR DISCORD\"",
                        "set hover event of {_t} to a new hover event showing \"Clicky Clicky!\"",
                        "set click event of {_t} to a new click event to open url \"https://OurDiscord.com\"",
                        "send component {_t} to player")
                .since("1.5.0")
                .parser(new Parser<>() {
                    @Override
                    public @NotNull String toString(@NotNull ComponentWrapper o, int flags) {
                        return o.toString();
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toVariableNameString(@NotNull ComponentWrapper o) {
                        return o.toString();
                    }
                }));
    }

}
