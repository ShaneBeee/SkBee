package com.shanebeestudios.skbee.api.registry;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.GameEvent;
import org.bukkit.GameRule;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.MusicInstrument;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"UnstableApiUsage", "rawtypes", "deprecation", "removal", "NullableProblems", "unchecked", "DataFlowIssue"})
public class RegistryHolders {

    private static final Registry<BlockType> BLOCK_REGISTRY = RegistryUtils.getRegistry(RegistryKey.BLOCK);
    private static final Registry<org.bukkit.inventory.ItemType> ITEM_REGISTRY = RegistryUtils.getRegistry(RegistryKey.ITEM);
    private static final Map<String, RegistryHolder> REGISTRY_HOLDERS_BY_NAME = new HashMap<>();
    private static final Map<RegistryKey, RegistryHolder> REGISTRY_HOLDERS_BY_REGISTRY_KEY = new HashMap<>();

    static {
        // Only register once
        Comparator<TagKey, ItemType> itemTypeComparator = (tagKey, itemType) -> {
            Key key = itemType.getMaterial().key();

            if (tagKey.registryKey() == RegistryKey.BLOCK) {
                TypedKey<BlockType> typedKey = TypedKey.create(RegistryKey.BLOCK, key);
                return Relation.get(BLOCK_REGISTRY.getTag(tagKey).contains(typedKey));
            } else if (tagKey.registryKey() == RegistryKey.ITEM) {
                TypedKey<org.bukkit.inventory.ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, key);
                return Relation.get(ITEM_REGISTRY.getTag(tagKey).contains(typedKey));
            }
            return null;
        };
        register(RegistryKey.ATTRIBUTE, Attribute.class);
        register(RegistryKey.BIOME, Biome.class);
        register(RegistryKey.BLOCK, ItemType.class, blockType -> new ItemType(blockType.asMaterial()), itemTypeComparator);
        if (Util.IS_RUNNING_MC_1_21_5) {
            register(RegistryKey.CHICKEN_VARIANT, Chicken.Variant.class);
            register(RegistryKey.COW_VARIANT, Cow.Variant.class);
        }
        register(RegistryKey.DAMAGE_TYPE, DamageType.class);
        register(RegistryKey.DATA_COMPONENT_TYPE, DataComponentType.class);
        if (Util.IS_RUNNING_MC_1_21_7) {
            register(RegistryKey.DIALOG, Dialog.class);
        }
        register(RegistryKey.ENCHANTMENT, Enchantment.class);
        register(RegistryKey.ENTITY_TYPE, EntityType.class);
        register(RegistryKey.FROG_VARIANT, Frog.Variant.class);
        register(RegistryKey.GAME_EVENT, GameEvent.class);
        if (Util.IS_RUNNING_MC_1_21_11) {
            register(RegistryKey.GAME_RULE, GameRule.class);
        }
        register(RegistryKey.INSTRUMENT, MusicInstrument.class);
        register(RegistryKey.ITEM, ItemType.class, itemType -> new ItemType(itemType.asMaterial()));
        register(RegistryKey.JUKEBOX_SONG, JukeboxSong.class);
        register(RegistryKey.MEMORY_MODULE_TYPE, MemoryKey.class);
        register(RegistryKey.MOB_EFFECT, PotionEffectType.class);
        register(RegistryKey.PARTICLE_TYPE, Particle.class);
        register(RegistryKey.POTION, PotionType.class);
        register(RegistryKey.SOUND_EVENT, String.class, soundEvent -> soundEvent.key().toString());
        register(RegistryKey.STRUCTURE, Structure.class);
        register(RegistryKey.STRUCTURE_TYPE, StructureType.class);
        register(RegistryKey.TRIM_MATERIAL, TrimMaterial.class);
        register(RegistryKey.TRIM_PATTERN, TrimPattern.class);
        register(RegistryKey.VILLAGER_PROFESSION, Villager.Profession.class);
        register(RegistryKey.VILLAGER_TYPE, Villager.Type.class);
        if (Util.IS_RUNNING_MC_1_21_5) {
            register(RegistryKey.WOLF_VARIANT, Wolf.Variant.class);
        }
        if (Util.IS_RUNNING_MC_1_21_11) {
            register(RegistryKey.ZOMBIE_NAUTILUS_VARIANT, ZombieNautilus.Variant.class);
        }
    }

    private static <F extends Keyed, T extends F> void register(RegistryKey<F> key, Class<T> returnType) {
        register(key, returnType, null, (o1, o2) -> {
            TypedKey<F> typedKey = TypedKey.create(o1.registryKey(), o2.key());
            return Relation.get(RegistryAccess.registryAccess().getRegistry(o1.registryKey()).getTag(o1).contains(typedKey));
        });
    }

    private static <F extends Keyed, T> void register(RegistryKey<F> key, Class<T> returnType, @Nullable Converter<F, T> converter) {
        register(key, returnType, converter, null);
    }

    private static <F extends Keyed, T> void register(RegistryKey<F> key, Class<T> returnType, @Nullable Converter<F, T> converter, @Nullable Comparator<TagKey, T> tagComparator) {
        String name = key.key().value();
        name = name.substring(name.lastIndexOf("/") + 1);
        name = name + " registry";
        RegistryHolder<F, T> registryHolder = new RegistryHolder<>(key, returnType, name, converter);
        REGISTRY_HOLDERS_BY_NAME.put(name, registryHolder);
        String nameNoUnderscore = name.replace("_", " ");
        if (!REGISTRY_HOLDERS_BY_NAME.containsKey(nameNoUnderscore)) {
            REGISTRY_HOLDERS_BY_NAME.put(nameNoUnderscore, registryHolder);
        }
        REGISTRY_HOLDERS_BY_REGISTRY_KEY.put(key, registryHolder);

        Class<TagKey> tkc = TagKey.class;
        if (tagComparator != null) {
            Comparators.registerComparator(tkc, returnType, tagComparator);
        }
    }

    public static String getDocUsage() {
        List<String> docNames = new ArrayList<>();
        REGISTRY_HOLDERS_BY_REGISTRY_KEY.forEach((key, holder) -> docNames.add(holder.getDocString()));
        Collections.sort(docNames);
        return String.join("\n", docNames);
    }

    @SuppressWarnings("rawtypes")
    public static Supplier<Iterator<RegistryKey>> getSupplier() {
        List<RegistryKey> keys = new ArrayList<>();
        REGISTRY_HOLDERS_BY_REGISTRY_KEY.keySet().stream().sorted(java.util.Comparator.comparing(Object::toString)).forEach(keys::add);
        return keys::iterator;
    }

    public static Parser<RegistryKey<?>> createParser() {
        return new Parser<>() {
            @Override
            public @Nullable RegistryKey<?> parse(String string, ParseContext context) {
                string = string.replace("minecraft:", "");
                RegistryHolder registryHolder = REGISTRY_HOLDERS_BY_NAME.get(string);
                if (registryHolder != null) return registryHolder.getRegistryKey();
                return null;
            }

            @Override
            public String toString(RegistryKey<?> registryKey, int flags) {
                return registryKey.key() + " registry";
            }

            @Override
            public String toVariableNameString(RegistryKey<?> registryKey) {
                return toString(registryKey, 0);
            }
        };
    }

    /**
     * Get a {@link RegistryHolder} based on a {@link RegistryKey}
     *
     * @param key Key of holder
     * @return Holder from key
     */
    @NotNull
    public static RegistryHolder getRegistryHolder(RegistryKey<?> key) {
        return REGISTRY_HOLDERS_BY_REGISTRY_KEY.get(key);
    }

}
