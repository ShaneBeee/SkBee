package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
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
import org.bukkit.EntityEffect;
import org.bukkit.GameEvent;
import org.bukkit.GameRule;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Name("Available Objects")
@Description({"Get a list of all available objects of a specific type.",
    "SPECIAL TYPES:",
    "`materials` = All materials as ItemTypes (will be a list of blocks and items).",
    "`itemtypes` = All item materials as ItemTypes.",
    "`blocktypes` = All block materials as ItemTypes."})
@Examples({"give player random element of all available itemtypes",
    "set {_blocks::*} to all available blocktypes",
    "set target block to random element of all available blockdatas"})
@Since("1.15.0")
@SuppressWarnings({"NullableProblems", "rawtypes", "deprecation"})
public class ExprAvailableMaterials extends SimpleExpression<Object> {

    static {
        // Register materials as itemtypes and blockdata
        List<ItemType> materials = new ArrayList<>();
        List<ItemType> itemTypes = new ArrayList<>();
        List<ItemType> blockTypes = new ArrayList<>();
        List<BlockData> blockDatas = new ArrayList<>();

        Arrays.stream(Material.values())
            .filter(material -> !material.isLegacy())
            .sorted(Comparator.comparing(Material::getKey))
            .forEach(material -> {
                ItemType itemType = new ItemType(material);
                materials.add(itemType);
                if (material.isItem()) {
                    itemTypes.add(itemType);
                }
                if (material.isBlock()) {
                    blockTypes.add(itemType);
                    blockDatas.add(material.createBlockData());
                }
            });
        Registration.registerList("materials", ItemType.class, materials);
        Registration.registerList("item[ ]types", ItemType.class, itemTypes);
        Registration.registerList("block[ ]types", ItemType.class, blockTypes);
        Registration.registerList("block[ ]datas", BlockData.class, blockDatas);


        // Register entity types
        // Using a map to prevent adding doubles to a list
        Map<String, EntityData> entityDataMap = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            Class<? extends Entity> entityClass = entityType.getEntityClass();
            if (entityClass == null) {
                continue;
            }
            EntityData<?> entityData = EntityUtils.toSkriptEntityData(entityType);
            //noinspection ConstantValue
            if (entityData == null) {
                Util.debug("Skript is missing EntityType: %s", entityType.getKey());
                continue;
            }
            entityDataMap.put(entityData.toString(), entityData);
        }
        List<EntityData> entityDatas = entityDataMap.values().stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
        Registration.registerList("entity[ ]types", EntityData.class, entityDatas);

        // Register enums (which don't have a registry)
        List<GameRule> gameRules = Arrays.asList(GameRule.values());
        gameRules = gameRules.stream().sorted(Comparator.comparing(GameRule::getName)).collect(Collectors.toList());
        Registration.registerList("game[ ]rules", GameRule.class, gameRules);

        List<LootTable> lootTables = new ArrayList<>();
        Registry.LOOT_TABLES.forEach(lt -> {
            LootTable lootTable = lt.getLootTable();
            // Bukkit claims this is NotNull but the method it grabs from is indeed Nullable
            //noinspection ConstantValue
            if (lootTable != null) lootTables.add(lootTable);
        });
        Registration.registerList("loot tables", LootTable.class, lootTables.stream().sorted(Comparator.comparing(lootTable -> lootTable.getKey().getKey())).toList());

        List<Particle> particles = ParticleUtil.getAvailableParticles();
        particles = particles.stream().sorted(Comparator.comparing(ParticleUtil::getName)).collect(Collectors.toList());
        Registration.registerList("particles", Particle.class, particles);

        @SuppressWarnings("deprecation")
        List<PotionEffectType> potions = Arrays.asList(PotionEffectType.values());
        potions = potions.stream().sorted(Comparator.comparing(potionEffectType -> potionEffectType.getKey().getKey())).collect(Collectors.toList());
        Registration.registerList("potion effect types", PotionEffectType.class, potions);

        List<EntityEffect> entityEffects = Arrays.asList(EntityEffect.values());
        entityEffects = entityEffects.stream().sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
        Registration.registerList("entity effects", EntityEffect.class, entityEffects);

        // Register registries
        Registration.registerRegistry("attributes", Attribute.class, Registry.ATTRIBUTE);
        Registration.registerRegistry("biomes", Biome.class, Registry.BIOME);
        if (Skript.fieldExists(Registry.class, "DAMAGE_TYPE")) {
            Registration.registerRegistry("damage types", DamageType.class, Registry.DAMAGE_TYPE);
        }
        Registration.registerRegistry("enchantments", Enchantment.class, Registry.ENCHANTMENT);
        Registration.registerRegistry("game events", GameEvent.class, Registry.GAME_EVENT);
        Registration.registerRegistry("minecraft entity[ ]types", EntityType.class, Registry.ENTITY_TYPE);
        Registration.registerRegistry("statistics", Statistic.class, Registry.STATISTIC);

        if (Types.HAS_ARMOR_TRIM) {
            Registration.registerRegistry("trim materials", TrimMaterial.class, Registry.TRIM_MATERIAL);
            Registration.registerRegistry("trim patterns", TrimPattern.class, Registry.TRIM_PATTERN);
        }

        // Register registries as strings (this may be for types Skript doesn't have)
        Registration.registerStrings("sounds", Registry.SOUNDS);

        Skript.registerExpression(ExprAvailableMaterials.class, Object.class, ExpressionType.SIMPLE,
            Registration.getPatterns());
    }

    private Registration registration;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.registration = Registration.REGISTRATIONS.get(matchedPattern);
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        return this.registration.getObjects();
    }

    @Override
    public @Nullable Iterator<?> iterator(Event event) {
        return this.registration.objects.iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return this.registration.type;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return this.registration.getToString();
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
            items = items.stream().sorted(Comparator.comparing(item -> item.getKey().toString())).collect(Collectors.toList());
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
            return REGISTRATIONS.stream().map(Registration::getPattern).toList().toArray(new String[0]);
        }

        String pattern;
        Class<T> type;
        List<T> objects;
        String name;

        public Registration(String pattern, Class<T> type, List<T> objects) {
            this.pattern = pattern;
            this.type = type;
            this.objects = objects;
            this.name = pattern.replace("[ ]", " ");
        }

        String getPattern() {
            return "[all] available " + this.pattern;
        }

        String getToString() {
            return "all available " + this.name;
        }

        Object[] getObjects() {
            return this.objects.toArray(new Object[0]);
        }

    }

}
