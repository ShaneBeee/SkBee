package com.shanebeestudios.skbee.elements.text.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Types {

    static {
        // Allow components to be used anywhere a string can
        Converters.registerConverter(ComponentWrapper.class, String.class, ComponentWrapper::toString);
        Comparators.registerComparator(ComponentWrapper.class, ComponentWrapper.class, (o1, o2) -> Relation.get(o1.equals(o2)));

        Changer<ComponentWrapper> COMP_CHANGER = new Changer<>() {
            @Override
            public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                if (mode == ChangeMode.ADD) return CollectionUtils.array(HoverEvent.class, ClickEvent.class, ComponentWrapper.class);
                return null;
            }

            @Override
            public void change(ComponentWrapper[] components, @Nullable Object[] delta, ChangeMode mode) {
                if (delta == null || delta.length == 0) return;
                if (mode == ChangeMode.ADD) {
                    for (ComponentWrapper component : components) {
                        if (delta[0] instanceof HoverEvent<?> hoverEvent) {
                            component.setHoverEvent(hoverEvent);
                        } else if (delta[0] instanceof ClickEvent clickEvent) {
                            component.setClickEvent(clickEvent);
                        } else if (delta[0] instanceof ComponentWrapper cw) {
                            component.append(cw);
                        }
                    }
                }
            }
        };

        Classes.registerClass(new ClassInfo<>(ComponentWrapper.class, "textcomponent")
            .user("text ?components?")
            .name("TextComponent - Text Component")
            .description("Text components used for hover/click events. Due to the complexity of these, ",
                "they can NOT be long term stored in variables. \n\bRequires a PaperMC server.")
            .examples("set {_t} to text component from \"CLICK FOR OUR DISCORD\"",
                "add hover event showing \"Clicky Clicky!\" to {_t}",
                "add click event to open url \"https://OurDiscord.com\" to {_t}",
                "send component {_t} to player")
            .since("1.5.0")
            .cloner(original -> ComponentWrapper.fromComponent(original.getComponent()))
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
            })
            .changer(COMP_CHANGER)
            .serializer(new Serializer<>() {
                @Override
                public Fields serialize(ComponentWrapper component) throws NotSerializableException {
                    Fields fields = new Fields();
                    fields.putObject("json", component.toJsonString());
                    return fields;
                }

                @Override
                protected ComponentWrapper deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                    if (!fields.hasField("json"))
                        throw new StreamCorruptedException("TextComponent is missing the 'json' tag");
                    try {
                        return ComponentWrapper.fromJson(fields.getObject("json", String.class));
                    } catch (StreamCorruptedException exception) {
                        throw new StreamCorruptedException("TextComponent JSON failed to convert back into a component, possibly caused by version changes.");
                    }
                }

                @Override
                public void deserialize(ComponentWrapper component, Fields fields) throws StreamCorruptedException, NotSerializableException {
                    assert false : "This should never by called by skript";
                }

                @Override
                public boolean mustSyncDeserialization() {
                    return true;
                }

                @Override
                protected boolean canBeInstantiated() {
                    return false;
                }
            })
        );

        if (Skript.classExists("net.kyori.adventure.chat.SignedMessage") && Classes.getExactClassInfo(SignedMessage.class) == null) {
            Classes.registerClass(new ClassInfo<>(SignedMessage.class, "signedmessage")
                .user("signed ?messages?")
                .name("Signed Chat Message")
                .description("Represents a signed chat message.")
                .examples("remove all players from signed chat message # will remove the message from the client")
                .parser(SkriptUtils.getDefaultParser())
                .since("3.5.0")
                .changer(new Changer<>() {
                    @Override
                    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL)
                            return CollectionUtils.array(Player[].class);
                        return null;
                    }

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

        if (Classes.getExactClassInfo(TagResolver.class) == null) {
            Classes.registerClass(new ClassInfo<>(TagResolver.class, "tagresolver")
                .user("tag ?resolvers?")
                .description("Represents an object to replace text in a mini message.")
                .examples("# Create a component",
                    "set {_t} to translate component of player's tool",
                    "add hover event showing player's tool to {_t}",
                    "# Use this component in the resolver to replace \"<item>\" in the mini message",
                    "set {_r} to resolver(\"item\", {_t})",
                    "# setup the mini message with the replacement placeholder",
                    "set {_m} to mini message from \"<rainbow> Hey guys check out my <item> aint she a beaut?\" with {_r}",
                    "send component {_m}")
                .parser(SkriptUtils.getDefaultParser())
                .since("3.5.0"));
        }

        ClassInfo<Audience> audienceClassInfo = new ClassInfo<>(Audience.class, "audience")
            .user("audiences?")
            .name("TextComponent - Audience")
            .description("Represents things in Minecraft (players, entities, worlds, console, etc) which can receive media (messages, bossbars, action bars, etc).")
            .defaultExpression(new EventValueExpression<>(CommandSender.class))
            .parser(SkriptUtils.getDefaultParser())
            .after("commandsender", "player", "livingentity", "entity")
            .since("3.8.0");
        Classes.registerClass(audienceClassInfo);
        setupUsage(audienceClassInfo);

        // Functions
        Functions.registerFunction(new SimpleJavaFunction<>("resolver", new Parameter[]{
            new Parameter<>("placeholder", DefaultClasses.STRING, true, null),
            new Parameter<>("replacement", DefaultClasses.OBJECT, true, null),
            new Parameter<>("parsable", DefaultClasses.BOOLEAN, true, new SimpleLiteral<>(false, true)),
        }, Classes.getExactClassInfo(TagResolver.class), true) {
            @SuppressWarnings("PatternValidation")
            @Override
            public TagResolver @Nullable [] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) return null;

                String key = (String) params[0][0];
                if (key == null) return null;

                Object object = params[1][0];
                Boolean parsable = (Boolean) params[2][0];
                if (parsable == null) parsable = false;

                TagResolver resolver;
                if (object instanceof ComponentWrapper component) {
                    resolver = Placeholder.component(key, component.getComponent());
                } else if (object instanceof Component component) {
                    resolver = Placeholder.component(key, component);
                } else if (parsable) {
                    resolver = Placeholder.parsed(key, Classes.toString(object));
                } else {
                    resolver = Placeholder.component(key, ComponentWrapper.fromText(Classes.toString(object)).getComponent());
                }
                return new TagResolver[]{resolver};
            }
        }
            .description("Creates a tag resolver for replacements in mini message.",
                "`placeholder` = The string that will be replaced in the mini message.",
                "In the mini message itself this part needs to be surrounded by <>. See examples!",
                "`replacement` = A string/text component that will replace the first string.",
                "`parsable` = Whether the replacement should be parsed as mini message after being replaced.",
                "For servers that take in user inputs, this should be used with caution.")
            .examples("# Create a component",
                "set {_t} to translate component of player's tool",
                "add hover event showing player's tool to {_t}",
                "# Use this component in the resolver to replace \"<item>\" in the mini message",
                "set {_r} to resolver(\"item\", {_t})",
                "# setup the mini message with the replacement placeholder",
                "set {_m} to mini message from \"<rainbow> Hey guys check out my <item> aint she a beaut?\" with {_r}",
                "send component {_m}",
                "",
                "on async chat:",
                "\tset {_chatColor} to resolver(\"chatcolor\", raw ({chatcolor::%player%} ? raw \"<gray>\"), true)",
                "\tset async chat message to mini message from \"<chatcolor>%async chat message%\" with resolver {_chatColor}")
            .since("3.5.0"));
    }

    @SuppressWarnings("DataFlowIssue")
    private static void setupUsage(ClassInfo<Audience> audienceClassInfo) {
        // Make sure all class infos are created before creating usage
        TaskUtils.getGlobalScheduler().runTaskLater(() -> {
            List<String> names = new ArrayList<>();
            Classes.getExactClassInfo(ClassInfo.class).getSupplier().get().forEachRemaining(classInfo -> {
                if (Audience.class.isAssignableFrom(classInfo.getC()) && classInfo.getC() != Audience.class) {
                    String docName = classInfo.getDocName();
                    if (docName != null && !docName.isEmpty()) names.add(docName);
                }
            });
            Collections.sort(names);
            String usage = String.join(", ", names);
            audienceClassInfo.usage("Skript Types that are considered audiences:", usage);
        }, 1);
    }

}
