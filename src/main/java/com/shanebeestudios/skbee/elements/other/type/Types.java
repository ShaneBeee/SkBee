package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.BukkitUtils;
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
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.region.TaskUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.wrapper.RegistryClassInfo;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Chunk.LoadLevel;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"removal", "deprecation", "UnstableApiUsage", "rawtypes"})
public class Types {

    static {
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
            EnumWrapper<Spellcaster.Spell> SPELL_ENUM = new EnumWrapper<>(Spellcaster.Spell.class);
            Classes.registerClass(SPELL_ENUM.getClassInfo("spell")
                .user("spells?")
                .name("Spellcaster Spell")
                .description("Represents the different spells of a spellcaster.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("1.17.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'spell' already.");
            Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
        }

        // Only register if no other addons have registered this class

        if (Classes.getExactClassInfo(NamespacedKey.class) == null) {
            Classes.registerClass(new ClassInfo<>(NamespacedKey.class, "namespacedkey")
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

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void deserialize(NamespacedKey o, Fields f) {
                    }

                    @SuppressWarnings("NullableProblems")
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
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'namespaced key' already.");
            Util.logLoading("You may have to use their NamespacedKeys in SkBee's synaxes.");
        }

        if (Classes.getExactClassInfo(BlockFace.class) == null) {
            EnumWrapper<BlockFace> BLOCK_FACE_ENUM = new EnumWrapper<>(BlockFace.class, "", "face");
            Classes.registerClass(BLOCK_FACE_ENUM.getClassInfo("blockface")
                .user("blockfaces?")
                .name("BlockFace")
                .description("Represents the face of a block.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("2.6.0")
                .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true)));
        } else {
            Util.logLoading("It looks like another addon registered 'blockFace' already.");
            Util.logLoading("You may have to use their BlockFace in SkBee's syntaxes.");
        }

        if (Skript.methodExists(PlayerRespawnEvent.class, "getRespawnReason") || Skript.classExists("io.papermc.paper.event.player.AbstractRespawnEvent")) {
            if (Classes.getExactClassInfo(RespawnReason.class) == null) {
                EnumWrapper<RespawnReason> RESPAWN_REASON_ENUM = new EnumWrapper<>(RespawnReason.class, "", "respawn");
                Classes.registerClass(RESPAWN_REASON_ENUM.getClassInfo("respawnreason")
                    .user("respawn ?reasons?")
                    .name("Respawn Reason")
                    .description("Represents the reason the respawn event was called. Requires MC 1.19.4+",
                        "NOTE: These are auto-generated and may differ between server versions.")
                    .examples("on respawn:",
                        "\tif respawn reason = death respawn:",
                        "\t\tgive player 10 diamonds")
                    .since("2.8.4"));
            } else {
                Util.logLoading("It looks like another addon registered 'respawn reason' already.");
                Util.logLoading("You may have to use their RespawnReason in SkBee's syntaxes.");
            }
        }

        if (Classes.getExactClassInfo(BlockState.class) == null) {
            Classes.registerClass(new ClassInfo<>(BlockState.class, "blockstate")
                .user("blockstates?")
                .name("BlockState")
                .description("Represents a captured state of a block, which will not change automatically.",
                    "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
                    "In a structure, this represents how the block is saved to the structure.",
                    "Requires MC 1.17.1+")
                .since("1.12.3")
                .parser(new Parser<>() {
                    @SuppressWarnings("NullableProblems")
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
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'blockState' already.");
            Util.logLoading("You may have to use their BlockState in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(ArmorTrim.class) == null) {
            Classes.registerClass(new ClassInfo<>(ArmorTrim.class, "armortrim")
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
                }));
        }

        if (Classes.getExactClassInfo(TrimMaterial.class) == null) {
            Classes.registerClass(RegistryClassInfo.create(Registry.TRIM_MATERIAL, TrimMaterial.class, "trimmaterial", null, "material")
                .user("trim ?materials?")
                .name("ArmorTrim - TrimMaterial")
                .description("Represents a material that may be used in an ArmorTrim.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("2.13.0"));
        }

        if (Classes.getExactClassInfo(TrimPattern.class) == null) {
            Classes.registerClass(RegistryClassInfo.create(Registry.TRIM_PATTERN, TrimPattern.class, "trimpattern", null, "pattern")
                .user("trim ?patterns?")
                .name("ArmorTrim - TrimPattern")
                .description("Represents a pattern that may be used in an ArmorTrim.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("2.13.0"));
        }

        if (Classes.getExactClassInfo(LoadLevel.class) == null) {
            EnumWrapper<LoadLevel> LOAD_LEVEL_ENUM = new EnumWrapper<>(LoadLevel.class, "", "level");
            Classes.registerClass(LOAD_LEVEL_ENUM.getClassInfo("chunkloadlevel")
                .user("chunk ?load ?levels?")
                .name("Chunk Load Level")
                .description("Represents the types of load levels of a chunk.",
                    "- `border_level` = Most game logic is not processed, including entities and redstone.",
                    "- `entity_ticking_level` = All game logic is processed.",
                    "- `inaccessible_level` = No game logic is processed, world generation may still occur.",
                    "- `ticking_level` = All game logic except entities is processed.",
                    "- `unloaded_level` = This chunk is not loaded.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("2.17.0"));
        }

        if (Classes.getExactClassInfo(EntityEffect.class) == null) {
            EnumWrapper<EntityEffect> ENTITY_EFFECT_ENUM = new EnumWrapper<>(EntityEffect.class);
            Classes.registerClass(ENTITY_EFFECT_ENUM.getClassInfo("entityeffect")
                .user("entit(y|ies) ?effects?")
                .name("Entity Effect")
                .description("Represents an effect that can be played on an entity.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("3.0.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'EntityEffect' already.");
            Util.logLoading("You may have to use their EntityEffects in SkBee's 'play entity effect' effect.");
        }

        if (Classes.getExactClassInfo(MemoryKey.class) == null) {
            //noinspection unchecked
            Classes.registerClass(RegistryClassInfo.create(Registry.MEMORY_MODULE_TYPE, (Class) MemoryKey.class, "memory")
                .user("memor(y|ies)")
                .name("Memory")
                .description("Represents the different memories of an entity.",
                    "NOTE: These are auto-generated and may differ between server versions."));
        } else {
            Util.logLoading("It looks like another addon registered 'memory' already.");
            Util.logLoading("You may have to use their ItemFlags in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Action.class) == null) {
            EnumWrapper<Action> ACTION_ENUM = new EnumWrapper<>(Action.class);
            Classes.registerClass(ACTION_ENUM.getClassInfo("blockaction")
                .user("block ?actions?")
                .name("Block Action")
                .description("Represents different wants to interact.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("3.4.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'blockaction' already.");
            Util.logLoading("You may have to use their BlockAction in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(EntityRemoveEvent.Cause.class) == null) {
            EnumWrapper<EntityRemoveEvent.Cause> CAUSE_ENUM = new EnumWrapper<>(EntityRemoveEvent.Cause.class);
            Classes.registerClass(CAUSE_ENUM.getClassInfo("entityremovecause")
                .user("entity ?remove ?causes?")
                .name("Entity Remove Cause")
                .description("Represents the reasons an entity was removed from the world.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .after("damagecause", "damagetype")
                .since("3.4.0"));
        }

        if (Classes.getExactClassInfo(PlayerSpawnChangeEvent.Cause.class) == null) {
            EnumWrapper<PlayerSpawnChangeEvent.Cause> CAUSE_ENUM = new EnumWrapper<>(PlayerSpawnChangeEvent.Cause.class);
            Classes.registerClass(CAUSE_ENUM.getClassInfo("playerspawnchangereason")
                .user("player ?spawn ?change ?reasons?")
                .name("Player Spawn Change Reason")
                .description("Represents the reasons why a player changed their spawn location.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .after("damagecause", "damagetype", "itemtype")
                .since("3.4.0"));
        }

        if (Classes.getExactClassInfo(EntityType.class) == null) {
            Classes.registerClass(RegistryClassInfo.create(Registry.ENTITY_TYPE, EntityType.class, "minecraftentitytype")
                .user("minecraft ?entity ?types?")
                .name("Minecraft - EntityType")
                .description("Represents a Minecraft entity.",
                    "These differ slightly from Skript's EntityType as the names match Minecraft namespaces.",
                    "These also support the use of the Minecraft namespace as well as underscores.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .examples("mc spawn sheep at player",
                    "mc spawn minecraft:sheep at player",
                    "mc spawn minecraft:armor_stand at player")
                .after("entitydata", "entitydata")
                .since("3.5.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'minecraftEntityType' already.");
            Util.logLoading("You may have to use their Minecraft EntityType in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Color.class) == null) {
            Classes.registerClass(new ClassInfo<>(Color.class, "bukkitcolor")
                .user("bukkit ?colors?")
                .name("Bukkit Color")
                .description("Represents a Bukkit color. This is different than a Skript color",
                    "as it adds an alpha channel.")
                .since("2.8.0")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
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
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'bukkitColor' already.");
            Util.logLoading("You may have to use their Color in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(TreeType.class) == null) {
            EnumWrapper<TreeType> TREE_TYPE = new EnumWrapper<>(TreeType.class, "", "tree");
            Classes.registerClass(TREE_TYPE.getClassInfo("bukkittreetype")
                .user("bukkit ?tree ?types?")
                .name("Bukkit Tree Type")
                .description("Represents the different types of trees.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .after("structuretype")
                .since("3.5.3"));
        } else {
            Util.logLoading("It looks like another addon registered 'tree' already.");
            Util.logLoading("You may have to use their TreeType in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Pose.class) == null) {
            EnumWrapper<Pose> POSE = new EnumWrapper<>(Pose.class, "", "pose");
            Classes.registerClass(POSE.getClassInfo("pose")
                .user("poses?")
                .name("Entity Pose")
                .description("Represents the pose of an entity.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("3.5.4"));
        } else {
            Util.logLoading("It looks like another addon registered 'pose' already.");
            Util.logLoading("You may have to use their Pose in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(EquipmentSlotGroup.class) == null) {
            // This class is not an enum, and does not have a registry
            Map<String, EquipmentSlotGroup> equipmentSlotGroups = SkriptUtils.getEquipmentSlotGroups();
            Classes.registerClass(new ClassInfo<>(EquipmentSlotGroup.class, "equipmentslotgroup")
                .user("equipment ?slot ?groups?")
                .name("Equipment Slot Group")
                .description("Represents different groups of equipment slots.",
                    "NOTE: These are auto-generated and may differ between server versions.")
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
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'equipmentSlotGroup' already.");
            Util.logLoading("You may have to use their EquipmentSlotGroup in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(AttributeModifier.class) == null) {
            Classes.registerClass(new ClassInfo<>(AttributeModifier.class, "attributemodifier")
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
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'attributeModifier' already.");
            Util.logLoading("You may have to use their AttributeModifier in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(AttributeModifier.Operation.class) == null) {
            Classes.registerClass(new EnumWrapper<>(AttributeModifier.Operation.class).getClassInfo("attributeoperation")
                .user("attribute ?operations?")
                .name("Attribute Modifier Operation")
                .description("Represents the different operations of an attribute modifer.",
                    "NOTE: These are auto-generated and may differ between server versions."));
        } else {
            Util.logLoading("It looks like another addon registered 'attributeOperation' already.");
            Util.logLoading("You may have to use their AttributeModifier Operation in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(PotionType.class) == null) {
            if (BukkitUtils.registryExists("POTION")) {
                Classes.registerClass(RegistryClassInfo.create(Registry.POTION, PotionType.class, "potiontype")
                    .user("potion ?types?")
                    .name("Potion Type")
                    .description("Represents the different types of potions (not potion effect types) used in vanilla potion items.")
                    .after("potioneffecttype", "itemtype")
                    .since("3.8.0"));
            }
        } else {
            Util.logLoading("It looks like another addon registered 'potiontype' already.");
            Util.logLoading("You may have to use their PotionType in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(MusicInstrument.class) == null) {
            if (BukkitUtils.registryExists("INSTRUMENT")) {
                Classes.registerClass(RegistryClassInfo.create(Registry.INSTRUMENT, MusicInstrument.class, "instrument")
                    .user("instruments?")
                    .name("Instrument")
                    .description("Represents the instruments used by goat horns.",
                        "Requires Minecraft 1.20.6+",
                        "NOTE: These are auto-generated and may differ between server versions.")
                    .since("3.8.0"));
            }
        } else {
            Util.logLoading("It looks like another addon registered 'instrument' already.");
            Util.logLoading("You may have to use their Instruments in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(JukeboxSong.class) == null) {
            if (BukkitUtils.registryExists("JUKEBOX_SONG")) {
                Classes.registerClass(RegistryClassInfo.create(Registry.JUKEBOX_SONG, JukeboxSong.class, "jukeboxsong")
                    .user("jukebox ?songs?")
                    .name("Instrument")
                    .description("Represents the songs for jukeboxes.",
                        "Requires Minecraft 1.21+",
                        "NOTE: These are auto-generated and may differ between server versions.")
                    .since("3.8.0"));
            }
        } else {
            Util.logLoading("It looks like another addon registered 'jukeboxson' already.");
            Util.logLoading("You may have to use their JukeboxSongs in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(PlayerFailMoveEvent.FailReason.class) == null) {
            Classes.registerClass(new EnumWrapper<>(PlayerFailMoveEvent.FailReason.class).getClassInfo("failmovereason")
                .user("fail ?move ?reasons?")
                .description("The reason a player failed to move in a `player fail move` event.")
                .since("3.11.0"));
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
    }

    // FUNCTIONS
    static {
        //noinspection DataFlowIssue
        Functions.registerFunction(new SimpleJavaFunction<>("bukkitColor", new Parameter[]{
            new Parameter<>("alpha", DefaultClasses.NUMBER, true, null),
            new Parameter<>("red", DefaultClasses.NUMBER, true, null),
            new Parameter<>("green", DefaultClasses.NUMBER, true, null),
            new Parameter<>("blue", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(Color.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable Color[] executeSimple(Object[][] params) {
                int alpha = ((Number) params[0][0]).intValue();
                int red = ((Number) params[1][0]).intValue();
                int green = ((Number) params[2][0]).intValue();
                int blue = ((Number) params[3][0]).intValue();
                alpha = MathUtil.clamp(alpha, 0, 255);
                red = MathUtil.clamp(red, 0, 255);
                green = MathUtil.clamp(green, 0, 255);
                blue = MathUtil.clamp(blue, 0, 255);
                return new Color[]{Color.fromARGB(alpha, red, green, blue)};
            }
        }
            .description("Creates a new Bukkit Color using alpha (transparency), red, green and blue channels.",
                "Number values must be between 0 and 255.")
            .examples("set {_color} to bukkitColor(50,155,100,10)")
            .since("2.8.0"));
    }

    static {
        ClassInfo<Timespan.TimePeriod> timePeriodInfo = Classes.getExactClassInfo(Timespan.TimePeriod.class);
        if (timePeriodInfo == null) {
            timePeriodInfo = new EnumWrapper<>(Timespan.TimePeriod.class, true)
                .getClassInfo("timespanperiod")
                .user("time ?span ?periods?")
                .name("Timespan Period")
                .description("Represents the time periods of a Timespan.")
                .since("3.9.0");
            Classes.registerClass(timePeriodInfo);
        }

        // Temporary until Skript (maybe) adds this.
        Functions.registerFunction(new SimpleJavaFunction<>("timespan", new Parameter[]{
            new Parameter<>("time", DefaultClasses.NUMBER, true, null),
            new Parameter<>("timeperiod", timePeriodInfo, true, null),
        }, DefaultClasses.TIMESPAN, true) {
            @Override
            public Timespan @Nullable [] executeSimple(Object[][] params) {
                long time = ((Number) params[0][0]).longValue();
                Timespan.TimePeriod timePeriod = ((Timespan.TimePeriod) params[1][0]);
                if (time >= 0) {
                    return new Timespan[]{new Timespan(timePeriod, time)};
                }
                return null;
            }
        }
            .description("Create a new Timespan.")
            .examples("set {_time} to timespan(1, minute)",
                "set {_time} to timespan(10, minutes)",
                "set {_time} to timespan(3, ticks)",
                "set {_time} to timespan(1, hour) + timespan(10, minutes)")
            .since("3.9.0"));
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
