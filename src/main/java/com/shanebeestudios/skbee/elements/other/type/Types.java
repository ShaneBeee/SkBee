package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.beacon.BeaconTier;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.BlockStateWrapper;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.wrapper.RegistryWrapper;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Types {

    public static boolean HAS_ARMOR_TRIM = Skript.classExists("org.bukkit.inventory.meta.trim.ArmorTrim");
    private static final Map<String, ItemFlag> ITEM_FLAG_MAP = new HashMap<>();

    private static String getItemFlagNames() {
        List<String> flags = new ArrayList<>(ITEM_FLAG_MAP.keySet());
        Collections.sort(flags);
        return StringUtils.join(flags, ", ");
    }

    static {
        if (Classes.getExactClassInfo(ItemFlag.class) == null) {
            for (ItemFlag itemFlag : ItemFlag.values()) {
                String name = itemFlag.name().replace("HIDE_", "").toLowerCase(Locale.ROOT) + "_flag";
                ITEM_FLAG_MAP.put(name, itemFlag);
            }
            Classes.registerClass(new ClassInfo<>(ItemFlag.class, "itemflag")
                    .user("item ?flags?")
                    .name("Item Flag")
                    .description("Represents the different Item Flags that can be applied to an item.",
                            "NOTE: Underscores aren't required, you CAN use spaces.")
                    .usage(getItemFlagNames())
                    .since("2.1.0")
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(@NotNull ParseContext context) {
                            return true;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public @Nullable ItemFlag parse(String string, ParseContext context) {
                            String flag = string.replace(" ", "_");
                            if (ITEM_FLAG_MAP.containsKey(flag)) return ITEM_FLAG_MAP.get(flag);
                            return null;
                        }

                        @Override
                        public @NotNull String toString(ItemFlag itemFlag, int flags) {
                            String flag = itemFlag.name().replace("HIDE_", "") + "_FLAG";
                            return flag.toLowerCase(Locale.ROOT);
                        }

                        @Override
                        public @NotNull String toVariableNameString(ItemFlag itemFlag) {
                            return toString(itemFlag, 0);
                        }
                    }));
        } else {
            Util.logLoading("It looks like another addon registered 'itemflag' already.");
            Util.logLoading("You may have to use their Item Flags in SkBee's 'Hidden Item Flags' expression.");
        }

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
            EnumWrapper<Spellcaster.Spell> SPELL_ENUM = new EnumWrapper<>(Spellcaster.Spell.class);
            Classes.registerClass(new ClassInfo<>(Spellcaster.Spell.class, "spell")
                    .user("spells?")
                    .name("Spellcaster Spell")
                    .description("Represents the different spells of a spellcaster.")
                    .usage(SPELL_ENUM.getAllNames())
                    .since("1.17.0")
                    .parser(SPELL_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'spell' already.");
            Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
        }

        // Only register if no other addons have registered this class
        // EntityPotionEffectEvent.Cause
        if (Classes.getExactClassInfo(Cause.class) == null) {
            EnumWrapper<Cause> POTION_EFFECT_EVENT_CAUSE = new EnumWrapper<>(Cause.class, "", "effect");
            Classes.registerClass(new ClassInfo<>(Cause.class, "potioneffectcause")
                    .user("potion ?effect ?causes?")
                    .name("Potion Effect Cause")
                    .description("Represents the different causes of an entity potion effect event.")
                    .usage(POTION_EFFECT_EVENT_CAUSE.getAllNames())
                    .since("1.17.0")
                    .parser(POTION_EFFECT_EVENT_CAUSE.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'potioneffectcause' already.");
            Util.logLoading("You may have to use their potion effect causes in SkBee's 'Entity Potion Effect' event.");
        }

        if (Classes.getExactClassInfo(TransformReason.class) == null) {
            EnumWrapper<TransformReason> TRANSOFORM_REASON = new EnumWrapper<>(TransformReason.class);
            Classes.registerClass(new ClassInfo<>(TransformReason.class, "transformreason")
                    .user("transform ?reasons?")
                    .name("Transform Reason")
                    .description("Represents the different reasons for transforming in the entity transform event.")
                    .usage(TRANSOFORM_REASON.getAllNames())
                    .since("2.5.3")
                    .parser(TRANSOFORM_REASON.getParser()));
        }

        if (Skript.methodExists(PlayerQuitEvent.class, "getReason")) {
            if (Classes.getExactClassInfo(QuitReason.class) == null) {
                EnumWrapper<QuitReason> QUIT_REASON = new EnumWrapper<>(QuitReason.class);
                Classes.registerClass(new ClassInfo<>(QuitReason.class, "quitreason")
                        .user("quit ?reasons?")
                        .name("Quit Reason")
                        .description("Represents the different reasons for calling the player quit event (Requires Paper).")
                        .usage(QUIT_REASON.getAllNames())
                        .since("2.6.0")
                        .parser(QUIT_REASON.getParser()));
            }
        }

        if (Classes.getExactClassInfo(NamespacedKey.class) == null) {
            Classes.registerClass(new ClassInfo<>(NamespacedKey.class, "namespacedkey")
                    .user("namespacedkeys?")
                    .name("NamespacedKey")
                    .description("NamespacedKeys are a way to declare and specify game objects in Minecraft,",
                            "which can identify built-in and user-defined objects without potential ambiguity or conflicts.",
                            "For more information see Resource Location on McWiki <link>https://minecraft.fandom.com/wiki/Resource_location</link>")
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
            Classes.registerClass(new ClassInfo<>(BlockFace.class, "blockface")
                    .user("blockfaces?")
                    .name("BlockFace")
                    .description("Represents the face of a block.")
                    .usage(BLOCK_FACE_ENUM.getAllNames())
                    .since("2.6.0")
                    .parser(BLOCK_FACE_ENUM.getParser())
                    .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true)));
        }

        if (Skript.methodExists(PlayerRespawnEvent.class, "getRespawnReason")) {
            EnumWrapper<RespawnReason> RESPAWN_REASON_ENUM = new EnumWrapper<>(RespawnReason.class, "", "respawn");
            Classes.registerClass(new ClassInfo<>(RespawnReason.class, "respawnreason")
                    .user("respawn ?reasons?")
                    .name("Respawn Reason")
                    .description("Represents the reason the respawn event was called. Requires MC 1.19.4+")
                    .usage(RESPAWN_REASON_ENUM.getAllNames())
                    .examples("on respawn:",
                            "\tif respawn reason = death respawn:",
                            "\t\tgive player 10 diamonds")
                    .parser(RESPAWN_REASON_ENUM.getParser())
                    .since("2.8.4"));
        }

        Classes.registerClass(new ClassInfo<>(BlockStateWrapper.class, "blockstate")
                .user("blockstates?")
                .name("BlockState")
                .description("Represents a captured state of a block, which will not change automatically.",
                        "Unlike Block, which only one object can exist per coordinate, BlockState can exist multiple times for any given Block.",
                        "In a structure, this represents how the block is saved to the structure.",
                        "Requires MC 1.17.1+")
                .since("1.12.3"));

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

            RegistryWrapper<TrimMaterial> TRIM_REGISTRY = RegistryWrapper.wrap(Registry.TRIM_MATERIAL, null, "material");
            Classes.registerClass(new ClassInfo<>(TrimMaterial.class, "trimmaterial")
                    .user("trim ?materials?")
                    .name("ArmorTrim - TrimMaterial")
                    .description("Represents a material that may be used in an ArmorTrim.")
                    .usage(TRIM_REGISTRY.getNames())
                    .since("2.13.0")
                    .parser(TRIM_REGISTRY.getParser()));

            RegistryWrapper<TrimPattern> TRIM_PATTERN_REGISTER = RegistryWrapper.wrap(Registry.TRIM_PATTERN, null, "pattern");
            Classes.registerClass(new ClassInfo<>(TrimPattern.class, "trimpattern")
                    .user("trim ?patterns?")
                    .name("ArmorTrim - TrimPattern")
                    .description("Represents a pattern that may be used in an ArmorTrim.")
                    .usage(TRIM_PATTERN_REGISTER.getNames())
                    .since("2.13.0")
                    .parser(TRIM_PATTERN_REGISTER.getParser()));
        }

        EnumWrapper<BeaconTier> BEACON_TIER = new EnumWrapper<>(BeaconTier.class);
        Classes.registerClass(new ClassInfo<>(BeaconTier.class, "beacontier")
                .user("beacon ?tiers?")
                .name("Beacon Tier")
                .description("Represents a tier of beacon current placed with the world.")
                .since("INSERT VERSION")
                .usage(BEACON_TIER.getAllNames())
                .parser(BEACON_TIER.getParser()));

    }

}
