package com.shanebeestudios.skbee.api.registry;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"UnstableApiUsage", "rawtypes", "deprecation", "removal"})
public class RegistryUtils {

    private static final Map<String, RegistryHolder> REGISTRY_HOLDERS_BY_NAME = new HashMap<>();
    private static final Map<RegistryKey, RegistryHolder> REGISTRY_HOLDERS_BY_REGISTRY_KEY = new HashMap<>();

    static {
        register(RegistryKey.ATTRIBUTE, Attribute.class);
        register(RegistryKey.BIOME, Biome.class);
        register(RegistryKey.BLOCK, ItemType.class, blockType ->
            new ItemType(Objects.requireNonNull(blockType.asMaterial())));
        register(RegistryKey.ENCHANTMENT, Enchantment.class);
        register(RegistryKey.ENTITY_TYPE, EntityType.class);
        register(RegistryKey.DAMAGE_TYPE, DamageType.class);
        register(RegistryKey.GAME_EVENT, GameEvent.class);
        register(RegistryKey.ITEM, ItemType.class, itemType ->
            new ItemType(Objects.requireNonNull(itemType.asMaterial())));
        register(RegistryKey.MOB_EFFECT, PotionEffectType.class);
        register(RegistryKey.PARTICLE_TYPE, Particle.class);
        register(RegistryKey.SOUND_EVENT, String.class, soundEvent -> soundEvent.key().toString());
        register(RegistryKey.STRUCTURE, Structure.class);
        register(RegistryKey.TRIM_MATERIAL, TrimMaterial.class);
        register(RegistryKey.TRIM_PATTERN, TrimPattern.class);
        register(RegistryKey.VILLAGER_PROFESSION, Villager.Profession.class);
        register(RegistryKey.VILLAGER_TYPE, Villager.Type.class);
    }

    private static <F extends Keyed, T> void register(RegistryKey<F> key, Class<T> returnType) {
        register(key, returnType, null);
    }

    private static <F extends Keyed, T> void register(RegistryKey<F> key, Class<T> returnType, @Nullable Converter<F, T> converter) {
        String name = key.key().value();
        name = name.substring(name.lastIndexOf("/") + 1);
        name = name + " registry";
        RegistryHolder<F, T> registryHolder = new RegistryHolder<>(key, returnType, name, converter);
        REGISTRY_HOLDERS_BY_NAME.put(name, registryHolder);
        REGISTRY_HOLDERS_BY_REGISTRY_KEY.put(key, registryHolder);
    }

    public static String getDocUsage() {
        List<String> docNames = new ArrayList<>();
        REGISTRY_HOLDERS_BY_NAME.forEach((key, holder) -> {
            docNames.add(holder.getDocString());
        });
        Collections.sort(docNames);
        return String.join("<br>", docNames);
    }

    @SuppressWarnings("rawtypes")
    public static Supplier<Iterator<RegistryKey>> getSupplier() {
        List<RegistryKey> keys = new ArrayList<>();
        REGISTRY_HOLDERS_BY_NAME.forEach((key, holder) -> keys.add(holder.getRegistryKey()));
        return keys::iterator;
    }

    public static Parser<RegistryKey<?>> createParser() {
        return new Parser<>() {
            @Override
            public @Nullable RegistryKey<?> parse(String string, ParseContext context) {
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
