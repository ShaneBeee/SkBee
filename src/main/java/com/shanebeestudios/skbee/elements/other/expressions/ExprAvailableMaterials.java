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
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Name("Available Objects")
@Description({"Get a list of all available materials (will return as an itemtype, but it's a mix of blocks and items),",
        "itemtypes, block types (will return as an item type, but only materials which can be placed as a block), block datas,",
        "entity types, enchantments, potion effect types, biomes, game rules and particles (SkBee particles)."})
@Examples({"give player random element of all available itemtypes",
        "set {_blocks::*} to all available blocktypes",
        "set target block to random element of all available blockdatas"})
@Since("1.15.0")
@SuppressWarnings("NullableProblems")
public class ExprAvailableMaterials extends SimpleExpression<Object> {

    private static final List<ItemType> MATERIALS = new ArrayList<>();
    private static final List<ItemType> ITEM_TYPES = new ArrayList<>();
    private static final List<ItemType> BLOCK_TYPES = new ArrayList<>();
    private static final List<BlockData> BLOCK_DATAS = new ArrayList<>();
    private static final List<EntityData<?>> ENTITY_DATAS = new ArrayList<>();
    private static final List<Enchantment> ENCHANTMENTS = new ArrayList<>();
    private static final List<PotionEffectType> POTION_EFFECT_TYPES = new ArrayList<>();
    private static final List<Biome> BIOMES = new ArrayList<>();
    private static final List<GameRule<?>> GAME_RULES = new ArrayList<>();
    private static final List<Particle> PARTICLES = new ArrayList<>();

    static {
        List<Material> materials = Arrays.asList(Material.values());
        materials = materials.stream().sorted(Comparator.comparing(Enum::toString)).collect(Collectors.toList());
        for (Material material : materials) {
            ItemType itemType = new ItemType(material);
            MATERIALS.add(itemType);
            if (material.isItem()) {
                ITEM_TYPES.add(itemType);
            }
            if (material.isBlock()) {
                BLOCK_TYPES.add(itemType);
                BLOCK_DATAS.add(material.createBlockData());
            }
        }
        List<EntityType> entityTypes = Arrays.asList(EntityType.values());
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
                ENTITY_DATAS.add(entityData);
                continue;
            }
            // Prevent doubling up of entity types
            if (ENTITY_DATAS.contains(entityData)) {
                EntityData<?> superType = entityData.getSuperType();
                if (!ENTITY_DATAS.contains(superType)) {
                    ENTITY_DATAS.add(superType);
                }
                continue;
            }
            ENTITY_DATAS.add(entityData);
        }
        List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
        enchantments = enchantments.stream().sorted(Comparator.comparing(enchantment -> enchantment.getKey().getKey())).collect(Collectors.toList());
        ENCHANTMENTS.addAll(enchantments);

        List<PotionEffectType> potionEffectTypes = Arrays.asList(PotionEffectType.values());
        potionEffectTypes = potionEffectTypes.stream().sorted(Comparator.comparing(PotionEffectType::getName)).collect(Collectors.toList());
        POTION_EFFECT_TYPES.addAll(potionEffectTypes);

        List<Biome> biomes = Arrays.asList(Biome.values());
        biomes = biomes.stream().sorted(Comparator.comparing(biome -> biome.getKey().getKey())).collect(Collectors.toList());
        BIOMES.addAll(biomes);

        List<GameRule<?>> gameRules = Arrays.asList(GameRule.values());
        gameRules = gameRules.stream().sorted(Comparator.comparing(GameRule::getName)).collect(Collectors.toList());
        GAME_RULES.addAll(gameRules);

        List<Particle> particles = ParticleUtil.getAvailableParticles();
        particles = particles.stream().sorted(Comparator.comparing(ParticleUtil::getName)).collect(Collectors.toList());
        PARTICLES.addAll(particles);

        Skript.registerExpression(ExprAvailableMaterials.class, Object.class, ExpressionType.SIMPLE,
                "[all] available materials",
                "[all] available item[ ]types",
                "[all] available block[ ]types",
                "[all] available block[ ]datas",
                "[all] available entity[ ]types",
                "[all] available enchantments",
                "[all] available potion effect types",
                "[all] available biomes",
                "[all] available game[ ]rules",
                "[all] available particles");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event e) {
        return switch (pattern) {
            case 1 -> ITEM_TYPES.toArray(new ItemType[0]);
            case 2 -> BLOCK_TYPES.toArray(new ItemType[0]);
            case 3 -> BLOCK_DATAS.toArray(new BlockData[0]);
            case 4 -> ENTITY_DATAS.toArray(new EntityData[0]);
            case 5 -> ENCHANTMENTS.toArray(new Enchantment[0]);
            case 6 -> POTION_EFFECT_TYPES.toArray(new PotionEffectType[0]);
            case 7 -> BIOMES.toArray(new Biome[0]);
            case 8 -> GAME_RULES.toArray(new GameRule[0]);
            case 9 -> PARTICLES.toArray(new Particle[0]);
            default -> MATERIALS.toArray(new ItemType[0]);
        };
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return switch (pattern) {
            case 3 -> BlockData.class;
            case 4 -> EntityData.class;
            case 5 -> Enchantment.class;
            case 6 -> PotionEffectType.class;
            case 7 -> Biome.class;
            case 8 -> GameRule.class;
            case 9 -> Particle.class;
            default -> ItemType.class;
        };
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        String type = switch (pattern) {
            case 1 -> "itemtypes";
            case 2 -> "block types";
            case 3 -> "block datas";
            case 4 -> "entity datas";
            case 5 -> "enchantments";
            case 6 -> "potion effect types";
            case 7 -> "biomes";
            case 8 -> "game rules";
            case 9 -> "particles";
            default -> "materials";
        };
        return "all available " + type;
    }

}
