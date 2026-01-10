package com.shanebeestudios.skbee.elements.registry.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;

@SuppressWarnings({"rawtypes"})
public class Types {

    static {
        ClassInfo<RegistryKey> registryKeyClassInfo = new ClassInfo<>(RegistryKey.class, "registrykey")
            .user("registry ?keys?")
            .name("Registry - Registry Key")
            .description("Represents a key for a Minecraft registry.",
                "Values in square brackets resemble the Skript type linked to the registry.",
                Util.AUTO_GEN_NOTE)
            .parser(RegistryHolders.createParser())
            .supplier(RegistryHolders.getSupplier())
            .since("3.8.0");
        Classes.registerClass(registryKeyClassInfo);

        // Run later to make sure SkBee's classes have loaded
        TaskUtils.getGlobalScheduler().runTaskLater(() -> registryKeyClassInfo.usage(RegistryHolders.getDocUsage()), 1);

        Classes.registerClass(new ClassInfo<>(TagKey.class, "tagkey")
            .user("tag ?keys?")
            .name("Registry - Tag Key")
            .description("Represents a key for a Minecraft tag.",
                "TagKeys can also compare if it contains objects (Think of them like a list).")
            .examples("set {_tagkey} to tag key \"minecraft:wool\" from block registry",
                "if {_tagkey} contains player's tool:",
                "if biome registry tag key \"minecraft:is_forest\" contains biome at player:")
            .since("3.8.0")
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
            .since("3.8.0")
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
