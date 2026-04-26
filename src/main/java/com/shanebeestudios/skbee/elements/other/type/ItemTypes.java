package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.bukkitutil.BukkitUtils;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.Registry;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ItemTypes {

    public static void register(Registration reg) {
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
                .description("Represents the different operations of an attribute modifer.",
                    "See [**Attribute Operations**](https://minecraft.wiki/w/Attribute#Operations) on McWiki for more details.",
                    Util.AUTO_GEN_NOTE)
                .since("3.5.9")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'attributeOperation' already.");
            Util.logLoading("You may have to use their AttributeModifier Operation in SkBee's syntaxes.");
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

        if (Classes.getExactClassInfo(JukeboxSong.class) == null) {
            reg.newRegistryType(RegistryKey.JUKEBOX_SONG, JukeboxSong.class, "jukeboxsong")
                .user("jukebox ?songs?")
                .name("Jukebox Song")
                .description("Represents the songs for jukeboxes.", Util.AUTO_GEN_NOTE)
                .since("3.8.0")
                .register();
        } else {
            Util.logLoading("It looks like another addon registered 'jukeboxsong' already.");
            Util.logLoading("You may have to use their JukeboxSongs in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(MusicInstrument.class) == null) {
            if (BukkitUtils.registryExists("INSTRUMENT")) {
                reg.newRegistryType(RegistryKey.INSTRUMENT, MusicInstrument.class, "instrument")
                    .user("instruments?")
                    .name("Instrument")
                    .description("Represents the instruments used by goat horns.", Util.AUTO_GEN_NOTE)
                    .since("3.8.0")
                    .register();
            }
        } else {
            Util.logLoading("It looks like another addon registered 'instrument' already.");
            Util.logLoading("You may have to use their Instruments in SkBee's syntaxes.");
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

        if (Classes.getExactClassInfo(TrimMaterial.class) == null) {
            reg.newRegistryType(RegistryKey.TRIM_MATERIAL, TrimMaterial.class, "trimmaterial", null, "material")
                .user("trim ?materials?")
                .name("ArmorTrim - TrimMaterial")
                .description("Represents a material that may be used in an ArmorTrim.", Util.AUTO_GEN_NOTE)
                .since("2.13.0")
                .register();
        }

        if (Classes.getExactClassInfo(TrimPattern.class) == null) {
            reg.newRegistryType(RegistryKey.TRIM_PATTERN, TrimPattern.class, "trimpattern", null, "pattern")
                .user("trim ?patterns?")
                .name("ArmorTrim - TrimPattern")
                .description("Represents a pattern that may be used in an ArmorTrim.", Util.AUTO_GEN_NOTE)
                .since("2.13.0")
                .register();
        }

    }

}
