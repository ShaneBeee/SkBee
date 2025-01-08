package com.shanebeestudios.skbee.elements.registry.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Bukkit;

@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class Types {

    static {
        ClassInfo<RegistryKey> registryKeyClassInfo = new ClassInfo<>(RegistryKey.class, "registrykey")
            .user("registry ?keys?")
            .name("Registry - Registry Key")
            .description("Represents a key for a Minecraft registry.",
                "Values in square brackets resemble the Skript type linked to the registry.",
                "Registry names are auto-generated based on the Minecraft registry, these may change at any time.")
            .parser(RegistryHolders.createParser())
            .supplier(RegistryHolders.getSupplier())
            .since("INSERT VERSION");
        Classes.registerClass(registryKeyClassInfo);

        // Run later to make sure SkBee's classes have loaded
        Bukkit.getScheduler().runTaskLater(SkBee.getPlugin(), () ->
            registryKeyClassInfo.usage(RegistryHolders.getDocUsage()), 1);

        Classes.registerClass(new ClassInfo<>(TagKey.class, "tagkey")
            .user("tag ?keys?")
            .name("Registry - Tag Key")
            .description("Represents a key for a Minecraft tag.")
            .since("INSERT VERSION")
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(TagKey tagKey, int flags) {
                    return "#" + tagKey.key() + " (in " + Classes.toString(tagKey.registryKey()) + ")";
                }

                @Override
                public String toVariableNameString(TagKey tagKey) {
                    return toString(tagKey, 0);
                }
            }));

        Classes.registerClass(new ClassInfo<>(TypedKey.class, "typedkey")
            .user("typed ?keys?")
            .name("Registry - Typed Key")
            .description("Represents the key for a value in a Minecraft registry.")
            .since("INSERT VERSION")
            .parser(new Parser<>() {
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public String toString(TypedKey typedKey, int flags) {
                    return typedKey.key() + " (in " + Classes.toString(typedKey.registryKey()) + ")";
                }

                @Override
                public String toVariableNameString(TypedKey typedKey) {
                    return toString(typedKey, 0);
                }
            }));
    }

}
