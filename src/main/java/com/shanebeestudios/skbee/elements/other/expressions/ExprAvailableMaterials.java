package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Available Materials")
@Description({"Get a list of all available materials (will return as an itemtype, but it's a mix of blocks and items),",
        "itemtypes, block types (will return as an item type, but only materials which can be placed use as a block) and block datas."})
@Examples({"give player random element of all available itemtypes",
        "set {_blocks::*} to all available blocktypes",
        "set target block to random element of all available blockdatas"})
@Since("INSERT VERSION")
@SuppressWarnings("NullableProblems")
public class ExprAvailableMaterials extends SimpleExpression<Object> {

    private static final List<ItemType> MATERIALS = new ArrayList<>();
    private static final List<ItemType> ITEM_TYPES = new ArrayList<>();
    private static final List<ItemType> BLOCK_TYPES = new ArrayList<>();
    private static final List<BlockData> BLOCK_DATAS = new ArrayList<>();

    static {
        for (Material material : Material.values()) {
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
        Skript.registerExpression(ExprAvailableMaterials.class, Object.class, ExpressionType.SIMPLE,
                "[all] available materials",
                "[all] available item[ ]types",
                "[all] available block[ ]types",
                "[all] available block[ ]datas");
    }

    private int pattern;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event e) {
        switch (pattern) {
            case 1:
                return ITEM_TYPES.toArray(new ItemType[0]);
            case 2:
                return BLOCK_TYPES.toArray(new ItemType[0]);
            case 3:
                return BLOCK_DATAS.toArray(new BlockData[0]);
            default:
                return MATERIALS.toArray(new ItemType[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return pattern == 3 ? BlockData.class : ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        String type = "materials";
        switch (pattern) {
            case 1:
                type = "itemtypes";
                break;
            case 2:
                type = "block types";
                break;
            case 3:
                type = "block datas";
        }
        return "all available " + type;
    }

}
