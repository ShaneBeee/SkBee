package com.shanebeestudios.skbee.elements.advancement.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;

public class Types {

    static {
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Advancement.class) == null) {
            Classes.registerClass(new ClassInfo<>(Advancement.class, "advancement")
                    .user("advancements?")
                    .name("Advancement")
                    .description("Represents an advancement. These CAN be parsed, see examples.")
                    .examples("set {_a} to \"minecraft:nether/use_lodestone\" parsed as advancement")
                    .since("INSERT VERSION")
                    .parser(new Parser<>() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public @Nullable Advancement parse(String string, ParseContext context) {
                            NamespacedKey namespacedKey = NamespacedKey.fromString(string);
                            if (namespacedKey != null) {
                                return Bukkit.getAdvancement(namespacedKey);
                            }
                            return null;
                        }

                        @Override
                        public @NotNull String toString(Advancement advancement, int i) {
                            return advancement.getKey().toString();
                        }

                        @Override
                        public @NotNull String toVariableNameString(Advancement advancement) {
                            return "advancement:" + toString(advancement, 0);
                        }
                    })
                    .serializer(new Serializer<>() {
                        @Override
                        public @NotNull Fields serialize(Advancement advancement) throws NotSerializableException {
                            Fields fields = new Fields();
                            fields.putObject("advancement", advancement.getKey().toString());
                            return fields;
                        }

                        @Override
                        public void deserialize(Advancement advancement, Fields fields) {
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        protected Advancement deserialize(Fields fields) throws StreamCorruptedException {
                            String string = fields.getObject("advancement", String.class);
                            if (string != null) {
                                NamespacedKey namespacedKey = NamespacedKey.fromString(string);
                                if (namespacedKey != null) {
                                    return Bukkit.getAdvancement(namespacedKey);
                                }
                            }
                            return null;
                        }

                        @Override
                        public boolean mustSyncDeserialization() {
                            return false;
                        }

                        @Override
                        protected boolean canBeInstantiated() {
                            return false;
                        }
                    }));
        }

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(AdvancementProgress.class) == null) {
            Classes.registerClass(new ClassInfo<>(AdvancementProgress.class, "advancementpro")
                    .user("advancement ?progress(es)?")
                    .name("Advancement Progress")
                    .description("Represents the advancement progress of a player.",
                            "You will see `%advancementpro%` in the docs, this is due to a silly issue with Skript",
                            "where I couldn't use `progress` in expressions.")
                    .since("INSERT VERSION"));
        }
    }

}
