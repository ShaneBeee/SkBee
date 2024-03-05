package com.shanebeestudios.skbee.elements.text.type;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

public class Types {

    static {
        // Allow components to be used anywhere a string can
        Converters.registerConverter(ComponentWrapper.class, String.class, ComponentWrapper::toString);

        Changer<ComponentWrapper> COMP_CHANGER = new Changer<>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                if (mode == ChangeMode.ADD) return CollectionUtils.array(HoverEvent.class, ClickEvent.class);
                return null;
            }

            @SuppressWarnings({"NullableProblems", "ConstantValue"})
            @Override
            public void change(ComponentWrapper[] components, @Nullable Object[] delta, ChangeMode mode) {
                if (delta == null) return;
                if (mode == ChangeMode.ADD) {
                    for (ComponentWrapper component : components) {
                        if (delta[0] instanceof HoverEvent<?> hoverEvent) {
                            component.setHoverEvent(hoverEvent);
                        } else if (delta[0] instanceof ClickEvent clickEvent) {
                            component.setClickEvent(clickEvent);
                        }
                    }
                }
            }
        };

        Classes.registerClass(new ClassInfo<>(ComponentWrapper.class, "textcomponent")
                .user("text ?components?")
                .name("Text Component - Text Component")
                .description("Text components used for hover/click events. Due to the complexity of these, ",
                        "they can NOT be long term stored in variables. \n\bRequires a PaperMC server.")
                .examples("set {_t} to text component from \"CLICK FOR OUR DISCORD\"",
                        "add hover event showing \"Clicky Clicky!\" to {_t}",
                        "add click event to open url \"https://OurDiscord.com\" to {_t}",
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
                }).changer(COMP_CHANGER)
        );
    }

}
