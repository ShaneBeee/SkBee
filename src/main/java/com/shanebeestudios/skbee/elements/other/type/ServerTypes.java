package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class ServerTypes {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(Color.class) == null) {
            reg.newType(Color.class, "bukkitcolor")
                .user("bukkit ?colors?")
                .name("Bukkit Color")
                .description("Represents a Bukkit color. This is different than a Skript color",
                    "as it adds an alpha channel.")
                .since("2.8.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(Color bukkitColor, int flags) {
                        int alpha = bukkitColor.getAlpha();
                        int red = bukkitColor.getRed();
                        int green = bukkitColor.getGreen();
                        int blue = bukkitColor.getBlue();
                        return String.format("BukkitColor(a=%s,r=%s,g=%s,b=%s)", alpha, red, green, blue);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Color bukkitColor) {
                        return toString(bukkitColor, 0);
                    }
                })
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'bukkitColor' already.");
            Util.logLoading("You may have to use their Color in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(NamespacedKey.class) == null) {
            reg.newType(NamespacedKey.class, "namespacedkey")
                .user("namespacedkeys?")
                .name("NamespacedKey")
                .description("NamespacedKeys are a way to declare and specify game objects in Minecraft,",
                    "which can identify built-in and user-defined objects without potential ambiguity or conflicts.",
                    "For more information see [**Resource Location**](https://minecraft.wiki/w/Resource_location) on McWiki.")
                .since("2.6.0")
                .parser(SkriptUtils.getDefaultParser())
                .serializer(new Serializer<>() {
                    @Override
                    public @NotNull Fields serialize(NamespacedKey namespacedKey) {
                        Fields fields = new Fields();
                        fields.putObject("key", namespacedKey.toString());
                        return fields;
                    }

                    @Override
                    public void deserialize(NamespacedKey o, Fields f) {
                    }

                    @Override
                    protected NamespacedKey deserialize(Fields fields) throws StreamCorruptedException {
                        String key = fields.getObject("key", String.class);
                        if (key == null) {
                            throw new StreamCorruptedException("NamespacedKey string is null");
                        }
                        return NamespacedKey.fromString(key);
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
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'namespaced key' already.");
            Util.logLoading("You may have to use their NamespacedKeys in SkBee's synaxes.");
        }
    }

}
