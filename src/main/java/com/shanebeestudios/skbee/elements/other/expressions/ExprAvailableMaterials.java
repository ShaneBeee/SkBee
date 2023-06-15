package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.other.type.Types;
import org.bukkit.GameRule;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Name("Available Objects")
@Description({"Get a list of all available materials (will return as an itemtype, but it's a mix of blocks and items),",
        "itemtypes, block types (will return as an item type, but only materials which can be placed as a block), block datas,",
        "entity types, enchantments, potion effect types, biomes, game rules, particles (SkBee particles), sounds (as string),",
        "trim materials, and trim patterns."})
@Examples({"give player random element of all available itemtypes",
        "set {_blocks::*} to all available blocktypes",
        "set target block to random element of all available blockdatas"})
@Since("1.15.0")
@SuppressWarnings({"NullableProblems", "rawtypes"})
public class ExprAvailableMaterials extends SimpleExpression<Object> {

    static {
        // Register materials as itemtypes and blockdata
        List<Material> bukkitMaterials = Arrays.asList(Material.values());
        List<ItemType> materials = new ArrayList<>();
        List<ItemType> itemTypes = new ArrayList<>();
        List<ItemType> blockTypes = new ArrayList<>();
        List<BlockData> blockDatas = new ArrayList<>();

        bukkitMaterials = bukkitMaterials.stream().sorted(Comparator.comparing(Enum::toString)).collect(Collectors.toList());
        for (Material material : bukkitMaterials) {
            ItemType itemType = new ItemType(material);
            materials.add(itemType);
            if (material.isItem()) {
                itemTypes.add(itemType);
            }
            if (material.isBlock()) {
                blockTypes.add(itemType);
                blockDatas.add(material.createBlockData());
            }
        }
        Registration.registerList("materials", ItemType.class, materials);
        Registration.registerList("item[ ]types", ItemType.class, itemTypes);
        Registration.registerList("block[ ]types", ItemType.class, blockTypes);
        Registration.registerList("block[ ]datas", BlockData.class, blockDatas);


        // Register entity types
        List<EntityType> entityTypes = Arrays.asList(EntityType.values());
        List<EntityData> entityDatas = new ArrayList<>();
        entityTypes = entityTypes.stream().sorted(Comparator.comparing(Enum::toString)).collect(Collectors.toList());
        for (EntityType entityType : entityTypes) {
            Class<? extends Entity> entityClass = entityType.getEntityClass();
            if (entityClass == null) {
                continue;
            }
            EntityData<?> entityData = EntityData.fromClass(entityClass);
            if (entityData.getType() == Entity.class) {
                Util.debug("Skript is missing EntityType: %s", entityType.getKey());
                continue;
            }
            // Silly minecart issues with Skript
            if (Minecart.class.isAssignableFrom(entityClass)) {
                entityDatas.add(entityData);
                continue;
            }
            // Prevent doubling up of entity types
            if (entityDatas.contains(entityData)) {
                EntityData<?> superType = entityData.getSuperType();
                if (!entityDatas.contains(superType)) {
                    entityDatas.add(superType);
                }
                continue;
            }
            entityDatas.add(entityData);
        }
        Registration.registerList("entity[ ]types", EntityData.class, entityDatas);

        // Register enums (which don't have a registry)
        List<GameRule> gameRules = Arrays.asList(GameRule.values());
        gameRules = gameRules.stream().sorted(Comparator.comparing(GameRule::getName)).collect(Collectors.toList());
        Registration.registerList("game[ ]rules", GameRule.class, gameRules);

        List<Particle> particles = ParticleUtil.getAvailableParticles();
        particles = particles.stream().sorted(Comparator.comparing(ParticleUtil::getName)).collect(Collectors.toList());
        Registration.registerList("particles", Particle.class, particles);

        // Register registries
        Registration.registerRegistry("enchantments", Enchantment.class, Registry.ENCHANTMENT);
        Registration.registerRegistry("potion effect types", PotionEffectType.class, Registry.POTION_EFFECT_TYPE);
        Registration.registerRegistry("biomes", Biome.class, Registry.BIOME);

        if (Types.HAS_ARMOR_TRIM) {
            Registration.registerRegistry("trim materials", TrimMaterial.class, Registry.TRIM_MATERIAL);
            Registration.registerRegistry("trim patterns", TrimPattern.class, Registry.TRIM_PATTERN);
        }

        // Register registries as strings (this may be for types Skript doesn't have)
        Registration.registerStrings("sounds", Registry.SOUNDS);

        Skript.registerExpression(ExprAvailableMaterials.class, Object.class, ExpressionType.SIMPLE,
                Registration.getPatterns());
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        return Registration.REGISTRATIONS.get(this.pattern).getItems();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return Registration.REGISTRATIONS.get(this.pattern).type;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return Registration.REGISTRATIONS.get(this.pattern).getToString();
    }

    @SuppressWarnings("SameParameterValue")
    static class Registration<T> {

        static final List<Registration<?>> REGISTRATIONS = new ArrayList<>();

        static <T> void registerList(String pattern, Class<T> type, List<T> items) {
            Registration<T> registration = new Registration<>(pattern, type, items);
            REGISTRATIONS.add(registration);
        }

        static <R extends Keyed> void registerRegistry(String pattern, Class<R> type, Registry<R> registry) {
            List<R> items = new ArrayList<>();
            registry.forEach(items::add);
            items = items.stream().sorted(Comparator.comparing(item -> item.getKey().getKey())).collect(Collectors.toList());
            registerList(pattern, type, items);
        }

        static <R extends Keyed> void registerStrings(String pattern, Registry<R> registry) {
            List<String> items = new ArrayList<>();
            for (R r : registry) {
                items.add(r.getKey().getKey());
            }
            items = items.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
            registerList(pattern, String.class, items);
        }

        static String[] getPatterns() {
            List<String> patterns = new ArrayList<>();
            REGISTRATIONS.forEach(registration -> patterns.add(registration.getPattern()));
            return patterns.toArray(new String[0]);
        }

        String pattern;
        Class<T> type;
        List<T> items;
        String name;

        public Registration(String pattern, Class<T> type, List<T> items) {
            this.pattern = pattern;
            this.type = type;
            this.items = items;
            this.name = pattern.replace("[ ]", " ");
        }

        String getPattern() {
            return "[all] available " + this.pattern;
        }

        String getToString() {
            return "all available " + this.name;
        }

        Object[] getItems() {
            return items.toArray(new Object[0]);
        }

    }

}
