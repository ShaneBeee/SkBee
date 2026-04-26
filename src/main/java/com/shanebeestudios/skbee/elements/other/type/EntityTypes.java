package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.registrations.Classes;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.RegistryClassInfo;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.memory.MemoryKey;

public class EntityTypes {

    public static void register(Registration reg) {
        if (Classes.getExactClassInfo(EntityType.class) == null) {
            reg.newRegistryType(Registry.ENTITY_TYPE, EntityType.class, "minecraftentitytype")
                .user("minecraft ?entity ?types?")
                .name("Minecraft - EntityType")
                .description("Represents a Minecraft entity.",
                    "These differ slightly from Skript's EntityType as the names match Minecraft namespaces.",
                    "These also support the use of the Minecraft namespace as well as underscores.", Util.AUTO_GEN_NOTE)
                .examples("mc spawn sheep at player",
                    "mc spawn minecraft:sheep at player",
                    "mc spawn minecraft:armor_stand at player")
                .after("entitydata", "entitydata")
                .since("3.5.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'minecraftEntityType' already.");
            Util.logLoading("You may have to use their Minecraft EntityType in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(MemoryKey.class) == null) {
            //noinspection unchecked,rawtypes
            Classes.registerClass(RegistryClassInfo.create(Registry.MEMORY_MODULE_TYPE, (Class) MemoryKey.class, "memory")
                .user("memor(y|ies)")
                .name("Memory")
                .description("Represents the different memories of an entity.", Util.AUTO_GEN_NOTE));
        } else {
            Util.logLoading("It looks like another addon registered 'memory' already.");
            Util.logLoading("You may have to use their ItemFlags in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Pose.class) == null) {
            reg.newEnumType(Pose.class, "pose", null, "pose")
                .user("poses?")
                .name("Entity Pose")
                .description("Represents the pose of an entity.", Util.AUTO_GEN_NOTE)
                .since("3.5.4")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'pose' already.");
            Util.logLoading("You may have to use their Pose in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
            reg.newEnumType(Spellcaster.Spell.class, "spellcasterspell")
                .user("spells?")
                .name("Spellcaster Spell")
                .description("Represents the different spells of a spellcaster.", Util.AUTO_GEN_NOTE)
                .since("1.17.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'spell' already.");
            Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
        }
    }

}
