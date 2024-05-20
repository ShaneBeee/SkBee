package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.wrapper.RegistryWrapper;
import org.bukkit.Chunk.LoadLevel;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

@SuppressWarnings({"rawtypes", "deprecation", "removal"})
public class Types {

    public static boolean HAS_ARMOR_TRIM = Skript.classExists("org.bukkit.inventory.meta.trim.ArmorTrim");
    public static boolean HAS_CHUNK_LOAD_LEVEL = Skript.classExists("org.bukkit.Chunk$LoadLevel");

    static {
        if (Classes.getExactClassInfo(ItemFlag.class) == null) {
            EnumWrapper<ItemFlag> ITEM_FLAGS = new EnumWrapper<>(ItemFlag.class);
            Classes.registerClass(ITEM_FLAGS.getClassInfo("itemflag")
                .user("item ?flags?")
                .name("ItemFlag")
                .description("Represents the different ItemFlags that can be applied to an item.",
                    "NOTE: Underscores aren't required, you CAN use spaces.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .since("3.4.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'itemflag' already.");
            Util.logLoading("You may have to use their ItemFlags in SkBee's 'Item Flags' expressions.");
        }

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
            EnumWrapper<Spellcaster.Spell> SPELL_ENUM = new EnumWrapper<>(Spellcaster.Spell.class);
            Classes.registerClass(SPELL_ENUM.getClassInfo("spell")
                .user("spells?")
                .name("Spellcaster Spell")
                .description("Represents the different spells of a spellcaster.")
                .since("1.17.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'spell' already.");
            Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
        }

        // Only register if no other addons have registered this class
        // EntityPotionEffectEvent.Cause
        if (Classes.getExactClassInfo(Cause.class) == null) {
            EnumWrapper<Cause> POTION_EFFECT_EVENT_CAUSE = new EnumWrapper<>(Cause.class, "", "effect");
            Classes.registerClass(POTION_EFFECT_EVENT_CAUSE.getClassInfo("potioneffectcause")
                .user("potion ?effect ?causes?")
                .name("Potion Effect Cause")
                .description("Represents the different causes of an entity potion effect event.")
                .since("1.17.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'potioneffectcause' already.");
            Util.logLoading("You may have to use their potion effect causes in SkBee's 'Entity Potion Effect' event.");
        }

        if (Classes.getExactClassInfo(NamespacedKey.class) == null) {
            Classes.registerClass(new ClassInfo<>(NamespacedKey.class, "namespacedkey")
                .user("namespacedkeys?")
                .name("NamespacedKey")
                .description("NamespacedKeys are a way to declare and specify game objects in Minecraft,",
                    "which can identify built-in and user-defined objects without potential ambiguity or conflicts.",
                    "For more information see [**Resource Location**](https://minecraft.wiki/w/Resource_location) on McWiki.")
                .since("2.6.0")
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
        }

        if (Classes.getExactClassInfo(BlockFace.class) == null) {
            EnumWrapper<BlockFace> BLOCK_FACE_ENUM = new EnumWrapper<>(BlockFace.class);
            Classes.registerClass(BLOCK_FACE_ENUM.getClassInfo("blockface")
                .user("blockfaces?")
                .name("BlockFace")
                .description("Represents the face of a block.")
                .since("2.6.0")
                .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true)));
        }

        if (Skript.methodExists(PlayerRespawnEvent.class, "getRespawnReason")) {
            EnumWrapper<RespawnReason> RESPAWN_REASON_ENUM = new EnumWrapper<>(RespawnReason.class, "", "respawn");
            Classes.registerClass(RESPAWN_REASON_ENUM.getClassInfo("respawnreason")
                .user("respawn ?reasons?")
                .name("Respawn Reason")
                .description("Represents the reason the respawn event was called. Requires MC 1.19.4+")
                .examples("on respawn:",
                    "\tif respawn reason = death respawn:",
                    "\t\tgive player 10 diamonds")
                .since("2.8.4"));
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
        }

        if (HAS_ARMOR_TRIM) {
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

            RegistryWrapper<TrimMaterial> TRIM_REGISTRY = RegistryWrapper.wrap(TrimMaterial.class, null, "material");
            Classes.registerClass(new ClassInfo<>(TrimMaterial.class, "trimmaterial")
                .user("trim ?materials?")
                .name("ArmorTrim - TrimMaterial")
                .description("Represents a material that may be used in an ArmorTrim.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .usage(TRIM_REGISTRY.getNames())
                .since("2.13.0")
                .parser(TRIM_REGISTRY.getParser()));

            RegistryWrapper<TrimPattern> TRIM_PATTERN_REGISTER = RegistryWrapper.wrap(TrimPattern.class, null, "pattern");
            Classes.registerClass(new ClassInfo<>(TrimPattern.class, "trimpattern")
                .user("trim ?patterns?")
                .name("ArmorTrim - TrimPattern")
                .description("Represents a pattern that may be used in an ArmorTrim.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .usage(TRIM_PATTERN_REGISTER.getNames())
                .since("2.13.0")
                .parser(TRIM_PATTERN_REGISTER.getParser()));
        }

        if (HAS_CHUNK_LOAD_LEVEL) {
            EnumWrapper<LoadLevel> LOAD_LEVEL_ENUM = new EnumWrapper<>(LoadLevel.class, "", "level");
            Classes.registerClass(LOAD_LEVEL_ENUM.getClassInfo("chunkloadlevel")
                .user("chunk ?load ?levels?")
                .name("Chunk Load Level")
                .description("Represents the types of load levels of a chunk.",
                    "\n`border_level` = Most game logic is not processed, including entities and redstone.",
                    "\n`entity_ticking_level` = All game logic is processed.",
                    "\n`inaccessible_level` = No game logic is processed, world generation may still occur.",
                    "\n`ticking_level` = All game logic except entities is processed.",
                    "\n`unloaded_level` = This chunk is not loaded.")
                .since("2.17.0"));
        }

        if (Classes.getExactClassInfo(EntityEffect.class) == null) {
            EnumWrapper<EntityEffect> ENTITY_EFFECT_ENUM = new EnumWrapper<>(EntityEffect.class);
            Classes.registerClass(ENTITY_EFFECT_ENUM.getClassInfo("entityeffect")
                .user("entit(y|ies) ?effects?")
                .name("Entity Effect")
                .description("Represents an effect that can be played on an entity.")
                .since("3.0.0"));
        } else {
            Util.logLoading("It looks like another addon registered 'EntityEffect' already.");
            Util.logLoading("You may have to use their EntityEffects in SkBee's 'play entity effect' effect.");
        }

        RegistryWrapper<MemoryKey> MEMORY_REGISTRY = RegistryWrapper.wrap(Registry.MEMORY_MODULE_TYPE);
        Classes.registerClass(new ClassInfo<>(MemoryKey.class, "memory")
            .user("memor(y|ies)")
            .name("Memory")
            .description("Represents the different memories of an entity.",
                "NOTE: These are auto-generated and may differ between server versions.")
            .usage(MEMORY_REGISTRY.getNames())
            .parser(MEMORY_REGISTRY.getParser()));

        if (Classes.getExactClassInfo(EquipmentSlot.class) == null) {
            EnumWrapper<EquipmentSlot> SLOT_ENUM = new EnumWrapper<>(EquipmentSlot.class, null, "slot");
            Classes.registerClass(SLOT_ENUM.getClassInfo("equipmentslot")
                .user("equipment ?slots?")
                .name("Equipment Slot")
                .description("")
                .since("3.4.0"));
        }

        if (Classes.getExactClassInfo(Action.class) == null) {
            EnumWrapper<Action> ACTION_ENUM = new EnumWrapper<>(Action.class);
            Classes.registerClass(ACTION_ENUM.getClassInfo("blockaction")
                .user("block ?actions?")
                .name("Block Action")
                .description("")
                .since("3.4.0"));
        }

        if (Classes.getExactClassInfo(LootTable.class) == null) {
            Classes.registerClass(new ClassInfo<>(LootTable.class, "loottable")
                .user("loot ?tables?")
                .name("LootTable")
                .description("Represents a LootTable.")
                .examples("set {_table} to loottable from key \"minecraft:chests/ancient_city\"")
                .since("3.4.0")
                .parser(new Parser<>() {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public @Nullable LootTable parse(String string, ParseContext context) {
                        return null;
                    }

                    @Override
                    public @NotNull String toString(LootTable lootTable, int flags) {
                        return "LootTable{" + lootTable.getKey() + "}";
                    }

                    @Override
                    public @NotNull String toVariableNameString(LootTable lootTable) {
                        return "loottable:" + lootTable.getKey();
                    }
                }));
        }

        if (Skript.classExists("org.bukkit.event.entity.EntityRemoveEvent") && Classes.getExactClassInfo(EntityRemoveEvent.Cause.class) == null) {
            EnumWrapper<EntityRemoveEvent.Cause> CAUSE_ENUM = new EnumWrapper<>(EntityRemoveEvent.Cause.class);
            Classes.registerClass(CAUSE_ENUM.getClassInfo("entityremovecause")
                .user("entity ?remove ?causes?")
                .name("Entity Remove Cause")
                .description("Represents the reasons an entity was removed from the world.")
                .after("damagecause", "damagetype")
                .since("3.4.0"));
        }

        if (Skript.classExists("org.bukkit.event.player.PlayerSpawnChangeEvent") && Classes.getExactClassInfo(PlayerSpawnChangeEvent.Cause.class) == null) {
            EnumWrapper<PlayerSpawnChangeEvent.Cause> CAUSE_ENUM = new EnumWrapper<>(PlayerSpawnChangeEvent.Cause.class);
            Classes.registerClass(CAUSE_ENUM.getClassInfo("playerspawnchangereason")
                .user("player ?spawn ?change ?reasons?")
                .name("Player Spawn Change Reason")
                .description("Represents the reasons why a player changed their spawn location.")
                .after("damagecause", "damagetype", "itemtype")
                .since("3.4.0"));
        }

        if (Classes.getExactClassInfo(EntityType.class) == null) {
            RegistryWrapper<EntityType> ENTITY_TYPE = RegistryWrapper.wrap(Registry.ENTITY_TYPE);
            Classes.registerClass(new ClassInfo<>(EntityType.class, "minecraftentitytype")
                .user("minecraft ?entity ?types?")
                .name("Minecraft - EntityType")
                .description("Represents a Minecraft entity.",
                    "These differ slightly from Skript's EntityType as the names match Minecraft namespaces.",
                    "These also support the use of the Minecraft namespace as well as underscores.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .examples("mc spawn sheep at player",
                    "mc spawn minecraft:sheep at player",
                    "mc spawn minecraft:armor_stand at player")
                .usage(ENTITY_TYPE.getNames())
                .after("entitydata", "entitydata")
                .since("3.5.0")
                .parser(ENTITY_TYPE.getParser()));
        }

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

}
