package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.BukkitUtils;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Chunk.LoadLevel;
import org.bukkit.Color;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.TreeType;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.common.function.DefaultFunction;

import java.io.StreamCorruptedException;
import java.util.Map;

//@SuppressWarnings({"removal", "deprecation", "rawtypes", "UnstableApiUsage"})
public class Types {

    public static void register(Registration reg) {
        // Only register if no other addons have registered this class
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

        // Only register if no other addons have registered this class

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

        if (Classes.getExactClassInfo(BlockFace.class) == null) {
            reg.newEnumType(BlockFace.class, "blockface", "", "face")
                .user("blockfaces?")
                .name("BlockFace")
                .description("Represents the face of a block.", Util.AUTO_GEN_NOTE)
                .since("2.6.0")
                .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true))
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockFace' already.");
            Util.logLoading("You may have to use their BlockFace in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(BlockState.class) == null) {
            reg.newType(BlockState.class, "blockstate")
                .user("blockstates?")
                .name("BlockState")
                .description("Represents a captured state of a block, which will not change automatically.",
                    "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
                    "In a structure, this represents how the block is saved to the structure.",
                    "Requires MC 1.17.1+")
                .since("1.12.3")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(BlockState blockState, int flags) {
                        return String.format("BlockState{type=%s,location=%s}",
                            blockState.getType(), blockState.getLocation());
                    }

                    @Override
                    public @NotNull String toVariableNameString(BlockState blockState) {
                        return toString(blockState, 0);
                    }
                })
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockState' already.");
            Util.logLoading("You may have to use their BlockState in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(ArmorTrim.class) == null) {
            reg.newType(ArmorTrim.class, "armortrim")
                .user("armor ?trims?")
                .name("ArmorTrim")
                .description("Represents an armor trim that may be applied to an item.",
                    "Requires MC 1.19.4+")
                .since("2.13.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    @SuppressWarnings("removal")
                    public @NotNull String toString(ArmorTrim o, int flags) {
                        String material = o.getMaterial().getKey().getKey();
                        String pattern = o.getPattern().getKey().getKey();
                        return String.format("ArmorTrim{material='%s',pattern='%s'}", material, pattern);
                    }

                    @Override
                    public @NotNull String toVariableNameString(ArmorTrim o) {
                        return toString(o, 0);
                    }
                })
                .register();
        }

        if (Classes.getExactClassInfo(TrimMaterial.class) == null) {
            reg.newRegistryType(Registry.TRIM_MATERIAL, TrimMaterial.class, "trimmaterial", null, "material")
                .user("trim ?materials?")
                .name("ArmorTrim - TrimMaterial")
                .description("Represents a material that may be used in an ArmorTrim.", Util.AUTO_GEN_NOTE)
                .since("2.13.0")
                .register();
        }

        if (Classes.getExactClassInfo(TrimPattern.class) == null) {
            reg.newRegistryType(Registry.TRIM_PATTERN, TrimPattern.class, "trimpattern", null, "pattern")
                .user("trim ?patterns?")
                .name("ArmorTrim - TrimPattern")
                .description("Represents a pattern that may be used in an ArmorTrim.", Util.AUTO_GEN_NOTE)
                .since("2.13.0")
                .register();
        }

        if (Classes.getExactClassInfo(LoadLevel.class) == null) {
            reg.newEnumType(LoadLevel.class, "chunkloadlevel", null, "level")
                .user("chunk ?load ?levels?")
                .name("Chunk Load Level")
                .description("Represents the types of load levels of a chunk.",
                    "- `border_level` = Most game logic is not processed, including entities and redstone.",
                    "- `entity_ticking_level` = All game logic is processed.",
                    "- `inaccessible_level` = No game logic is processed, world generation may still occur.",
                    "- `ticking_level` = All game logic except entities is processed.",
                    "- `unloaded_level` = This chunk is not loaded.")
                .since("2.17.0")
                .register();
        }

        if (Classes.getExactClassInfo(MemoryKey.class) == null) {
            //noinspection unchecked
            Classes.registerClass(RegistryClassInfo.create(Registry.MEMORY_MODULE_TYPE, (Class) MemoryKey.class, "memory")
                .user("memor(y|ies)")
                .name("Memory")
                .description("Represents the different memories of an entity.", Util.AUTO_GEN_NOTE));
        } else {
            Util.logLoading("It looks like another addon registered 'memory' already.");
            Util.logLoading("You may have to use their ItemFlags in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Action.class) == null) {
            reg.newEnumType(Action.class, "blockaction")
                .user("block ?actions?")
                .name("Block Action")
                .description("Represents different ways to interact.")
                .since("3.4.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'blockaction' already.");
            Util.logLoading("You may have to use their BlockAction in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(EntityRemoveEvent.Cause.class) == null) {
            reg.newEnumType(EntityRemoveEvent.Cause.class, "entityremovecause")
                .user("entity ?remove ?causes?")
                .name("Entity Remove Cause")
                .description("Represents the reasons an entity was removed from the world.", Util.AUTO_GEN_NOTE)
                .after("damagecause", "damagetype")
                .since("3.4.0")
                .register();
        }

        if (Classes.getExactClassInfo(PlayerSpawnChangeEvent.Cause.class) == null) {
            reg.newEnumType(PlayerSpawnChangeEvent.Cause.class, "playerspawnchangereason")
                .user("player ?spawn ?change ?reasons?")
                .name("Player Spawn Change Reason")
                .description("Represents the reasons why a player changed their spawn location.", Util.AUTO_GEN_NOTE)
                .after("damagecause", "damagetype", "itemtype")
                .since("3.4.0")
                .register();
        }

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

        if (Classes.getExactClassInfo(TreeType.class) == null) {
            reg.newEnumType(TreeType.class, "bukkittreetype", null, "tree")
                .user("bukkit ?tree ?types?")
                .name("Bukkit Tree Type")
                .description("Represents the different types of trees.", Util.AUTO_GEN_NOTE)
                .after("structuretype")
                .since("3.5.3")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'tree' already.");
            Util.logLoading("You may have to use their TreeType in SkBee's syntaxes.");
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

        if (Classes.getExactClassInfo(EquipmentSlotGroup.class) == null) {
            // This class is not an enum, and does not have a registry
            Map<String, EquipmentSlotGroup> equipmentSlotGroups = SkriptUtils.getEquipmentSlotGroups();
            reg.newType(EquipmentSlotGroup.class, "equipmentslotgroup")
                .user("equipment ?slot ?groups?")
                .name("Equipment Slot Group")
                .description("Represents different groups of equipment slots.", Util.AUTO_GEN_NOTE)
                .usage(StringUtils.join(equipmentSlotGroups.keySet().stream().sorted().toList(), ", "))
                .parser(new Parser<>() {
                    @Override
                    public @Nullable EquipmentSlotGroup parse(String string, ParseContext context) {
                        string = string.replace(" ", "_");
                        return equipmentSlotGroups.get(string);
                    }

                    @Override
                    public @NotNull String toString(EquipmentSlotGroup slot, int flags) {
                        return slot.toString();
                    }

                    @Override
                    public @NotNull String toVariableNameString(EquipmentSlotGroup slot) {
                        return slot.toString();
                    }
                })
                .since("3.5.9")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'equipmentSlotGroup' already.");
            Util.logLoading("You may have to use their EquipmentSlotGroup in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(AttributeModifier.class) == null) {
            reg.newType(AttributeModifier.class, "attributemodifier")
                .user("attribute ?modifiers?")
                .name("Attribute Modifier")
                .description("Represents an attribute modifier from an item/living entity.")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(AttributeModifier modifier, int flags) {
                        return ItemUtils.attributeModifierToString(modifier);
                    }

                    @Override
                    public @NotNull String toVariableNameString(AttributeModifier o) {
                        return toString(o, 0);
                    }
                })
                .since("3.5.9")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'attributeModifier' already.");
            Util.logLoading("You may have to use their AttributeModifier in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(AttributeModifier.Operation.class) == null) {
            reg.newEnumType(AttributeModifier.Operation.class, "attributeoperation")
                .user("attribute ?operations?")
                .name("Attribute Modifier Operation")
                .description("Represents the different operations of an attribute modifer.", Util.AUTO_GEN_NOTE)
                .since("3.5.9")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'attributeOperation' already.");
            Util.logLoading("You may have to use their AttributeModifier Operation in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(PotionType.class) == null) {
            if (BukkitUtils.registryExists("POTION")) {
                reg.newRegistryType(Registry.POTION, PotionType.class, "potiontype")
                    .user("potion ?types?")
                    .name("Potion Type")
                    .description("Represents the different types of potions (not potion effect types) used in vanilla potion items.")
                    .after("potioneffecttype", "itemtype")
                    .since("3.8.0")
                    .register();
            }
        } else {
            Util.logLoading("It looks like another addon registered 'potiontype' already.");
            Util.logLoading("You may have to use their PotionType in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(MusicInstrument.class) == null) {
            if (BukkitUtils.registryExists("INSTRUMENT")) {
                reg.newRegistryType(Registry.INSTRUMENT, MusicInstrument.class, "instrument")
                    .user("instruments?")
                    .name("Instrument")
                    .description("Represents the instruments used by goat horns.",
                        "Requires Minecraft 1.20.6+", Util.AUTO_GEN_NOTE)
                    .since("3.8.0")
                    .register();
            }
        } else {
            Util.logLoading("It looks like another addon registered 'instrument' already.");
            Util.logLoading("You may have to use their Instruments in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(JukeboxSong.class) == null) {
            if (BukkitUtils.registryExists("JUKEBOX_SONG")) {
                reg.newRegistryType(Registry.JUKEBOX_SONG, JukeboxSong.class, "jukeboxsong")
                    .user("jukebox ?songs?")
                    .name("Jukebox Song")
                    .description("Represents the songs for jukeboxes.",
                        "Requires Minecraft 1.21+", Util.AUTO_GEN_NOTE)
                    .since("3.8.0")
                    .register();
            }
        } else {
            Util.logLoading("It looks like another addon registered 'jukeboxson' already.");
            Util.logLoading("You may have to use their JukeboxSongs in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(PlayerFailMoveEvent.FailReason.class) == null) {
            reg.newEnumType(PlayerFailMoveEvent.FailReason.class, "failmovereason")
                .name("Fail Reason")
                .user("fail ?move ?reasons?")
                .description("The reason a player failed to move in a `player fail move` event.")
                .since("3.11.0")
                .register();
        }

        reg.newType(Audience.class, "audience")
            .user("audiences?")
            .name("TextComponent - Audience")
            .description("Represents things in Minecraft (players, entities, worlds, console, etc) which can receive media (messages, bossbars, action bars, etc).")
            .defaultExpression(new EventValueExpression<>(CommandSender.class))
            .parser(SkriptUtils.getDefaultParser())
            .after("commandsender", "player", "livingentity", "entity")
            .since("3.8.0")
            .register();

        if (Skript.classExists("io.papermc.paper.connection.PlayerConnection")) {
            if (Classes.getExactClassInfo(PlayerConnection.class) == null) {
                reg.newType(PlayerConnection.class, "playerconnection")
                    .user("player ?connections?")
                    .name("Player Connection")
                    .description("Represents the connection of a player in an async connect config event and custom click event.")
                    .defaultExpression(new EventValueExpression<>(PlayerConnection.class))
                    .parser(SkriptUtils.getDefaultParser())
                    .since("3.16.0")
                    .register();
            } else {
                Util.logLoading("It looks like another addon registered 'playerconnection' already.");
                Util.logLoading("You may have to use their PlayerConnection in SkBee's syntaxes.");
            }
        }

        if (Classes.getExactClassInfo(EntityKnockbackEvent.Cause.class) == null) {
            reg.newEnumType(EntityKnockbackEvent.Cause.class, "knockbackcause")
                .user("knockback ?causes?")
                .defaultExpression(new EventValueExpression<>(EntityKnockbackEvent.Cause.class))
                .name("Entity Knockback Cause")
                .description("Represents the cause of knockback in an entity knockback event")
                .since("3.16.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'knockbackcause' already.");
            Util.logLoading("You may have to use their KnockbackCause in SkBee's syntaxes.");
        }

        // FUNCTIONS
        DefaultFunction<Color> bukkitColor = DefaultFunction.builder(reg.getAddon(), "bukkitColor", Color.class)
            .parameter("alpha", Number.class)
            .parameter("red", Number.class)
            .parameter("green", Number.class)
            .parameter("blue", Number.class)
            .build(args -> {
                int alpha = ((Number) args.get("alpha")).intValue();
                int red = ((Number) args.get("red")).intValue();
                int green = ((Number) args.get("green")).intValue();
                int blue = ((Number) args.get("blue")).intValue();

                alpha = MathUtil.clamp(alpha, 0, 255);
                red = MathUtil.clamp(red, 0, 255);
                green = MathUtil.clamp(green, 0, 255);
                blue = MathUtil.clamp(blue, 0, 255);
                return Color.fromARGB(alpha, red, green, blue);
            });

        reg.newFunction(bukkitColor)
            .name("Bukkit Color")
            .description("Creates a new Bukkit Color using alpha (transparency), red, green and blue channels.",
                "Number values must be between 0 and 255.")
            .examples("set {_color} to bukkitColor(50,155,100,10)")
            .since("2.8.0")
            .register();

        if (Classes.getExactClassInfo(Timespan.TimePeriod.class) == null) {
            reg.newEnumType(Timespan.TimePeriod.class, "timespanperiod", true)
                .name("Time Period")
                .user("time ?span ?periods?")
                .description("Represents the time periods of a Timespan.")
                .since("3.9.0")
                .register();
        }

        DefaultFunction<Timespan> timeFunc = DefaultFunction.builder(reg.getAddon(), "timespan", Timespan.class)
            .parameter("time", Number.class)
            .parameter("timePeriod", Timespan.TimePeriod.class)
            .build(args -> {
                long time = ((Number) args.get("time")).longValue();
                Timespan.TimePeriod timePeriod = args.get("timePeriod");
                if (time >= 0) {
                    return new Timespan(timePeriod, time);
                }
                return null;
            });
        reg.newFunction(timeFunc)
            .name("TimeSpan")
            .description("Create a new Timespan.")
            .examples("set {_time} to timespan(1, minute)",
                "set {_time} to timespan(10, minutes)",
                "set {_time} to timespan(3, ticks)",
                "set {_time} to timespan(1, hour) + timespan(10, minutes)")
            .since("3.9.0")
            .register();
    }

}
