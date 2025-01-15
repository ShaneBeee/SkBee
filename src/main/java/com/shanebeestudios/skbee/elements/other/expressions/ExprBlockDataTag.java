package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.util.BlockDataUtils;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockData - Tag")
@Description("Get/set a block data tag of BlockData or a Block.")
@Examples({"set {_water} to block data tag \"waterlogged\" of event-block",
    "set block data tag \"waterlogged\" of {_blockData} to true",
    "set block data tag \"waterlogged\" of event-block to true",
    "set blockdata tag \"waterlogged\" of event-block without updates to true"})
@Since("1.0.0, 2.16.1 (Variable Support)")
public class ExprBlockDataTag extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBlockDataTag.class, Object.class, ExpressionType.COMBINED,
            "block[ ](data|state) tag %string% of %blocks/blockdatas% [1:without updates]");
    }

    private Expression<String> tag;
    private Expression<?> object;
    private boolean applyPhysics;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tag = (Expression<String>) exprs[0];
        this.object = exprs[1];
        this.applyPhysics = !parseResult.hasTag("1");
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        String tagString = this.tag.getSingle(event);
        if (tagString == null) return null;
        List<Object> list = new ArrayList<>();

        for (Object object : this.object.getArray(event)) {
            BlockData blockData;
            if (object instanceof Block block) blockData = block.getBlockData();
            else if (object instanceof BlockData bd) blockData = bd;
            else continue;

            Object value = BlockDataUtils.getBlockDataValueFromTag(blockData, tagString);
            if (value == null) continue;
            list.add(value);
        }

        return list.toArray(new Object[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        boolean update = true;
        String tag = this.tag.getSingle(event);
        List<BlockData> blockDataList = new ArrayList<>();

        for (Object object : this.object.getArray(event)) {
            if (object instanceof BlockData oldBlockData) {
                BlockData newBlockData = BlockDataUtils.setBlockDataTag(oldBlockData, tag, delta[0]);
                if (newBlockData == null) {
                    tagError("Invalid tag \"" + tag + "\" for this blockdata: " + oldBlockData.getAsString());
                    continue;
                }
                blockDataList.add(newBlockData);
            } else if (object instanceof Block block) {
                update = false;
                BlockData oldBlockData = block.getBlockData();
                BlockData newBlockData = BlockDataUtils.setBlockDataTag(oldBlockData, tag, delta[0]);
                if (newBlockData == null) {
                    tagError("Invalid tag \"" + tag + "\" for this block: " + oldBlockData.getAsString());
                    continue;
                }
                block.setBlockData(newBlockData, this.applyPhysics);
            }
        }
        // Apparently Block can't be changed, so we have to do this weird method
        // Only update the expression if it was BlockData
        if (!update) return;
        this.object.change(event, blockDataList.toArray(new BlockData[0]), ChangeMode.SET);
    }

    @Override
    public boolean isSingle() {
        return this.object.isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String updates = !this.applyPhysics ? " without updates" : "";
        return "block data tag " + this.tag.toString(e, d) + " of " + this.object.toString(e, d) + updates;
    }

    private void tagError(String message) {
        errorRegex(message, "tag \\\"\\w+\\\"");
    }

}
