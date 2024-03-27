package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.BukkitUnsafe;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class to convert {@link NamespacedKey NamespacedKeys} to Skript objects.
 *
 * @param <T> {@link ClassInfo} class for conversion type
 */
public abstract class ObjectConverter<T> {

    private static final Map<Class<?>, ObjectConverter<?>> CONVERTERS = new HashMap<>();

    private static <E> void register(Class<E> c, ObjectConverter<E> converter) {
        CONVERTERS.put(c, converter);
    }

    private static <E extends Keyed> void register(Class<E> c, Registry<E> registery) {
        ObjectConverter<E> objectConverter = new ObjectConverter<>() {
            @Override
            public E get(NamespacedKey key) {
                return registery.get(key);
            }
        };
        CONVERTERS.put(c, objectConverter);
    }

    /**
     * Get converter from Class
     *
     * @param c Class to grab converter for
     * @return ObjectConverter from class
     */
    public static ObjectConverter<?> getFromClass(Class<?> c) {
        return CONVERTERS.get(c);
    }

    /**
     * Get a list of names of all supported {@link ClassInfo ClassInfos}
     * <p>This will mainly be used to get names for docs</p>
     *
     * @return Names of all supported ClassInfos
     */
    @SuppressWarnings("unused")
    public static String getAllNames() {
        List<String> names = new ArrayList<>();
        for (Class<?> aClass : CONVERTERS.keySet()) {
            ClassInfo<?> exactClassInfo = Classes.getExactClassInfo(aClass);
            if (exactClassInfo == null) continue;
            String docName = exactClassInfo.getDocName();
            names.add(docName);
        }
        Collections.sort(names);
        return StringUtils.join(names, ", ");
    }

    static {
        register(Advancement.class, Registry.ADVANCEMENT);
        register(Attribute.class, Registry.ATTRIBUTE);
        register(Biome.class, Registry.BIOME);
        if (Skript.classExists("org.bukkit.damage.DamageType")) {
            register(DamageType.class, Registry.DAMAGE_TYPE);
        }
        register(Enchantment.class, Registry.ENCHANTMENT);
        register(EntityData.class, new ObjectConverter<>() {
            @Override
            public EntityData<?> get(NamespacedKey key) {
                EntityType entityType = Registry.ENTITY_TYPE.get(key);
                if (entityType != null) {
                    return EntityUtils.toSkriptEntityData(entityType);
                }
                return null;
            }
        });
        register(GameEvent.class, Registry.GAME_EVENT);
        register(ItemType.class, new ObjectConverter<>() {
            @Override
            public ItemType get(NamespacedKey key) {
                Material mat = BukkitUnsafe.getMaterialFromMinecraftId(key.toString());
                if (mat != null) return new ItemType(mat);
                return null;
            }
        });
        register(LootTable.class, new ObjectConverter<>() {
            @Override
            public @Nullable LootTable get(NamespacedKey key) {
                return Bukkit.getLootTable(key);
            }
        });
        // Added in Spigot 1.20.2 (oct 20/2023)
        if (Skript.methodExists(Particle.class, "getKey")) {
            register(Particle.class, Registry.PARTICLE_TYPE);
        }
        register(PotionEffectType.class, new ObjectConverter<>() {
            @SuppressWarnings("deprecation")
            @Override
            public PotionEffectType get(NamespacedKey key) {
                return PotionEffectType.getByKey(key);
            }
        });
        if (SkBee.getPlugin().getPluginConfig().ELEMENTS_STATISTIC)
            register(Statistic.class, Registry.STATISTIC);
        // Paper method
        if (Skript.methodExists(Bukkit.class, "getWorld", NamespacedKey.class)) {
            register(World.class, new ObjectConverter<>() {
                @Override
                public World get(NamespacedKey key) {
                    return Bukkit.getWorld(key);
                }
            });
        }
    }

    /**
     * Get object from {@link NamespacedKey}
     *
     * @param key Key to get object from
     * @return Object from key
     */
    @Nullable
    public abstract T get(NamespacedKey key);
}
