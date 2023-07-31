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
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Name("BlockData - Tag")
@Description("Get/set a block data tag of BlockData or a Block.")
@Examples({"set {_water} to block data tag \"waterlogged\" of event-block",
        "set block data tag \"waterlogged\" of {_blockData} to true",
        "set block data tag \"waterlogged\" of event-block to true",
        "set blockdata tag \"waterlogged\" of event-block without updates to true"})
@Since("1.0.0, INSERT VERSION (Variable Support)")
public class ExprBlockDataTag extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBlockDataTag.class, Object.class, ExpressionType.COMBINED,
                "block[ ](data|state) tag %string% of %blocks/blockdatas% [1:without updates]");
    }

    private Expression<String> tag;
    private Expression<?> object;
    private boolean applyPhysics;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tag = (Expression<String>) exprs[0];
        this.object = exprs[1];
        this.applyPhysics = !parseResult.hasTag("1");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        String tagString = this.tag.getSingle(event);
        if (tagString == null) return null;
        List<Object> list = new ArrayList<>();

        for (Object object : this.object.getArray(event)) {
            BlockData blockData;
            if (object instanceof Block block) blockData = block.getBlockData();
            else if (object instanceof BlockData bd) blockData = bd;
            else continue;

            String tag = getTag(blockData.getAsString(), tagString);
            if (tag == null) continue;

            if (isBoolean(tag)) {
                list.add(Boolean.valueOf(tag));
            } else if (isNumber(tag)) {
                list.add(Integer.parseInt(tag));
            } else {
                list.add(tag);
            }
        }

        return list.toArray(new Object[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        boolean update = true;
        String tag = this.tag.getSingle(event);
        List<BlockData> blockDataList = new ArrayList<>();

        for (Object object : this.object.getArray(event)) {
            if (object instanceof BlockData oldblockData) {
                BlockData newBlockData = changeBlockData(oldblockData, tag, delta[0]);
                blockDataList.add(newBlockData);
            } else if (object instanceof Block block) {
                BlockData newBlockData = changeBlockData(block.getBlockData(), tag, delta[0]);
                if (newBlockData == null) continue;
                block.setBlockData(newBlockData, this.applyPhysics);
                update = false;
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

    // Utils
    private boolean isNumber(String string) {
        return string.matches("\\d+");
    }

    private boolean isBoolean(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    @Nullable
    private String getTag(String data, String tag) {
        String[] sp = getData(data);
        if (sp != null) {
            for (String string : sp) {
                String[] s = string.split("=");
                if (s[0].equals(tag)) {
                    return s[1];
                }
            }
        }
        return null;
    }

    private String[] getData(String data) {
        String[] splits1 = data.split("\\[");
        if (splits1.length >= 2) {
            String[] splits2 = splits1[1].split("]");

            return splits2[0].split(",");
        }
        return null;
    }

    private BlockData changeBlockData(BlockData oldBlockData, String tag, Object value) {
        if (oldBlockData.getAsString().contains("[")) {
            String newData = oldBlockData.getMaterial().getKey() + "[" + tag.toLowerCase(Locale.ROOT) + "=" + value + "]";
            try {
                BlockData blockData = Bukkit.createBlockData(newData);
                return oldBlockData.merge(blockData);
            } catch (IllegalArgumentException ex) {
                Util.debug("Could not parse block data: %s", newData);
            }
        }
        return null;
    }

}
