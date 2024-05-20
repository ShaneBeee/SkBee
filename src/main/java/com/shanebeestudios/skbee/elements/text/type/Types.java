package com.shanebeestudios.skbee.elements.text.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
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

        if (Skript.classExists("net.kyori.adventure.chat.SignedMessage")) {
            Classes.registerClass(new ClassInfo<>(SignedMessage.class, "signedmessage")
                .user("signed ?messages?")
                .name("Signed Chat Message")
                .description("Represents a signed chat message.")
                .examples("remove all players from signed chat message # will remove the message from the client")
                .parser(SkriptUtils.getDefaultParser())
                .since("3.5.0")
                .changer(new Changer<>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL)
                            return CollectionUtils.array(Player[].class);
                        return null;
                    }

                    @SuppressWarnings({"NullableProblems", "ConstantValue"})
                    @Override
                    public void change(SignedMessage[] what, @Nullable Object[] delta, ChangeMode mode) {
                        if (delta == null) return;
                        for (SignedMessage signedMessage : what) {
                            if (!signedMessage.canDelete()) continue;

                            for (Object object : delta) {
                                if (object instanceof Player player) {
                                    player.deleteMessage(signedMessage);
                                }
                            }
                        }
                    }
                }));
        }

        Classes.registerClass(new ClassInfo<>(TagResolver.class, "tagresolver")
            .user("tag ?resolvers?")
            .description("Represents an object to replace text in a mini message.")
            .examples("# Create a component",
                "set {_i} to translate component of player's tool",
                "# Use this comonent in the resolver to replace \"<item>\" in the mini message",
                "set {_r::1} to resolver(\"item\", {_i})",
                "# setup the mini message with the replacement placeholder",
                "set {_m} to mini message from \"<rainbow> Hey guys check out my <item> aint she a beaut?\" with {_r::*}",
                "send component {_m}")
            .since("3.5.0"));

        // Functions
        //noinspection DataFlowIssue
        Functions.registerFunction(new SimpleJavaFunction<>("resolver", new Parameter[]{
            new Parameter<>("placeholder", DefaultClasses.STRING, true, null),
            new Parameter<>("replacement", DefaultClasses.OBJECT, true, null)
        }, Classes.getExactClassInfo(TagResolver.class), true) {
            @SuppressWarnings({"NullableProblems", "PatternValidation"})
            @Override
            public TagResolver @Nullable [] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) return null;

                String string = (String) params[0][0];
                if (string == null) return null;

                Object object = params[1][0];
                ComponentWrapper component;
                if (object instanceof String s) {
                    component = ComponentWrapper.fromText(s);
                } else if (object instanceof ComponentWrapper c) {
                    component = c;
                } else {
                    component = ComponentWrapper.fromText(Classes.toString(object));
                }
                return new TagResolver[]{Placeholder.component(string, component.getComponent())};
            }
        }
            .description("Creates a tag resolver for replacements in mini message.",
                "`placeholder` = The string that will be replaced in the mini message.",
                "In the mini message itself this part needs to be surrounded by <>. See examples!",
                "`replacement` = A string/text component that will replace the first string.")
            .examples("# Create a component",
                "set {_i} to translate component of player's tool",
                "# Use this comonent in the resolver to replace \"<item>\" in the mini message",
                "set {_r::1} to resolver(\"item\", {_i})",
                "# setup the mini message with the replacement placeholder",
                "set {_m} to mini message from \"<rainbow> Hey guys check out my <item> aint she a beaut?\" with {_r::*}",
                "send component {_m}")
            .since("3.5.0"));
    }

}
