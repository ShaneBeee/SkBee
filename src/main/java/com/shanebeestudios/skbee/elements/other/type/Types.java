package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Types {

    private static String getItemFlagNames() {
        List<String> flags = new ArrayList<>();
        for (ItemFlag flag : ItemFlag.values()) {
            String hide = flag.name().replace("HIDE_", "").toLowerCase(Locale.ROOT);
            hide += "_flag";
            flags.add(hide);
        }
        Collections.sort(flags);
        return StringUtils.join(flags, ", ");
    }

    static {
        // == TYPES ==

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(State.class) == null) {
            EnumUtils<State> FISH_STATE_ENUM = new EnumUtils<>(State.class);
            Classes.registerClass(new ClassInfo<>(State.class, "fishingstate")
                    .user("fish(ing)? ?states?")
                    .name("Fish Event State")
                    .usage(FISH_STATE_ENUM.getAllNames())
                    .since("1.15.2")
                    .parser(FISH_STATE_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'fishingstate' already.");
            Util.logLoading("You may have to use their fishing states in SkBee's 'Fish Event State' expression.");
        }

        if (Classes.getExactClassInfo(ItemFlag.class) == null) {
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
                            String flag = string.replace(" ", "_").toUpperCase(Locale.ROOT);
                            flag = "HIDE_" + flag.replace("_FLAG", "");
                            try {
                                return ItemFlag.valueOf(flag);
                            } catch (IllegalArgumentException ignore) {
                                return null;
                            }
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
            EnumUtils<Spellcaster.Spell> SPELL_ENUM = new EnumUtils<>(Spellcaster.Spell.class);
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
            EnumUtils<Cause> POTION_EFFECT_EVENT_CAUSE = new EnumUtils<>(Cause.class, "", "effect");
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
            EnumUtils<TransformReason> TRANSOFORM_REASON = new EnumUtils<>(TransformReason.class);
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
                EnumUtils<QuitReason> QUIT_REASON = new EnumUtils<>(QuitReason.class);
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
                    .since("2.6.0"));
        }

        if (Classes.getExactClassInfo(BlockFace.class) == null) {
            EnumUtils<BlockFace> BLOCK_FACE_ENUM = new EnumUtils<>(BlockFace.class);
            Classes.registerClass(new ClassInfo<>(BlockFace.class, "blockface")
                    .user("blockfaces?")
                    .name("BlockFace")
                    .description("Represents the face of a block.")
                    .usage(BLOCK_FACE_ENUM.getAllNames())
                    .since("2.6.0")
                    .parser(BLOCK_FACE_ENUM.getParser())
                    .defaultExpression(new SimpleLiteral<>(BlockFace.NORTH, true)));
        }
    }

}
