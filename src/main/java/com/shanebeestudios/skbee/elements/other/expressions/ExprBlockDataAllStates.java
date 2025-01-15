package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockData - All Variations")
@Description({"Returns a list of all possible blockdatas for a particular block type.",
    "Requires Paper 1.21.4+"})
@Examples({"set {_states::*} to blockdata states of oak log",
    "# output:",
    "# minecraft:oak_log[axis=x]",
    "# minecraft:oak_log[axis=y]",
    "# minecraft:oak_log[axis=z]",
    "loop all blockdata variations of wall torch:",
    "# output:",
    "# minecraft:wall_torch[facing=north]",
    "# minecraft:wall_torch[facing=south]",
    "# minecraft:wall_torch[facing=west]",
    "# minecraft:wall_torch[facing=east]"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprBlockDataAllStates extends SimpleExpression<BlockData> {

    static {
        if (Skript.classExists("org.bukkit.block.BlockType") && Skript.methodExists(BlockType.class, "createBlockDataStates")) {
            Skript.registerExpression(ExprBlockDataAllStates.class, BlockData.class, ExpressionType.COMBINED,
                "[all] [possible] block[ ]data (states|variations) of %itemtypes/blockdatas%");
        }
    }

    private Expression<?> objects;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.objects = LiteralUtils.defendExpression(exprs[0]);
        return LiteralUtils.canInitSafely(this.objects);
    }

    @Override
    protected BlockData @Nullable [] get(Event event) {
        List<BlockData> blockDatas = new ArrayList<>();
        for (Object object : this.objects.getArray(event)) {
            Material material = null;
            if (object instanceof ItemType itemType) {
                material = itemType.getMaterial();
            } else if (object instanceof BlockData blockData) {
                material = blockData.getMaterial();
            }
            if (material != null && material.isBlock()) {
                BlockType blockType = material.asBlockType();
                if (blockType != null) {
                    blockDatas.addAll(blockType.createBlockDataStates());
                }
            }
        }
        return blockDatas.toArray(new BlockData[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "all possible blockdata states of " + this.objects.toString(e, d);
    }

}
