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
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Name("Available Objects")
@Description({"Get a list of all available materials (will return as an itemtype, but it's a mix of blocks and items),",
        "itemtypes, block types (will return as an item type, but only materials which can be placed as a block), block datas",
        "and entity types."})
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
            if (entityClass != null) {
                EntityData<?> entityData = EntityData.fromClass(entityClass);
                ENTITY_DATAS.add(entityData);
            }
        }
        Skript.registerExpression(ExprAvailableMaterials.class, Object.class, ExpressionType.SIMPLE,
                "[all] available materials",
                "[all] available item[ ]types",
                "[all] available block[ ]types",
                "[all] available block[ ]datas",
                "[all] available entity[ ]types");
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
            default -> "materials";
        };
        return "all available " + type;
    }

}
