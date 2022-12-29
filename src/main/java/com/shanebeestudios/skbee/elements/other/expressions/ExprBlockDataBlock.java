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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Name("BlockData - Tags")
@Description({"Customize BlockData tags for blocks.. You can get all the tags in a BlockData or a specific tag.",
        "You can set a specific tag of BlockData."})
@Examples({"set {_data} to block data of target block of player", "set {_data::*} to block data tags of target block of player",
        "set {_water} to block data tag \"waterlogged\" of event-block",
        "set block data tag \"waterlogged\" of event-block to true"})
@Since("1.0.0")
public class ExprBlockDataBlock extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBlockDataBlock.class, Object.class, ExpressionType.PROPERTY,
                "block[ ](data|state) tags of %blocks%",
                "block[ ](data|state) tag %string% of %blocks%");
    }

    private Expression<String> tag;
    private Expression<Block> blocks;
    private int parse;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, @NotNull Kleenean kleenean, ParseResult parseResult) {
        this.tag = i == 1 ? (Expression<String>) exprs[0] : null;
        this.blocks = (Expression<Block>) exprs[i];
        this.parse = i;
        return true;
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event event) {
        List<Object> list = new ArrayList<>();
        String tagString = this.tag != null ? this.tag.getSingle(event) : "";
        for (Block block : this.blocks.getAll(event)) {
            if (parse == 1) {
                String tag = getTag(block.getBlockData().getAsString(), tagString);
                if (tag == null) continue;

                if (isBoolean(tag)) {
                    list.add(Boolean.valueOf(tag));
                } else if (isNumber(tag)) {
                    list.add(Integer.parseInt(tag));
                } else {
                    list.add(tag);
                }
            } else {
                String[] tags = getTags(block.getBlockData().getAsString());
                if (tags != null) {
                    list.addAll(Arrays.asList(tags));
                }
            }
        }
        return list.toArray();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Object.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(@NotNull Event e, Object[] delta, @NotNull ChangeMode mode) {
        String obj = delta == null ? "" : delta[0].toString();
        for (Block block : blocks.getAll(e)) {
            BlockData blockData;
            switch (parse) {
                // Tag "string"
                case 1:
                    BlockData oldData = block.getBlockData();
                    if (oldData.getAsString().contains("[")) { // only attempt to change data for a block that has possible data
                        String tag = this.tag != null ? this.tag.getSingle(e) : null;
                        if (tag == null) continue;

                        String newData = block.getType().getKey() + "[" + tag.toLowerCase(Locale.ROOT) + "=" + obj + "]";
                        try {
                            blockData = Bukkit.createBlockData(newData);
                            blockData = oldData.merge(blockData);
                            block.setBlockData(blockData);
                        } catch (IllegalArgumentException ex) {
                            Util.debug("Could not parse block data: %s", newData);
                        }
                    }
                    break;
                // Tags
                case 0:
                    // Don't think this will work, so we shall ignore it
                    return;
            }
        }

    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public boolean isSingle() {
        return parse == 1 && this.blocks.isSingle();
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        if (parse == 0) {
            return "block data tags of " + this.blocks.toString(e, d);
        } else {
            return "block data tag " + this.tag.toString(e, d) + " of " + this.blocks.toString(e, d);
        }
    }

    // Utils
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

    @Nullable
    private String[] getTags(String data) {
        String[] splitData = getData(data);
        List<String> tags = new ArrayList<>();
        if (splitData == null) return null;

        for (String splitDatum : splitData) {
            tags.add(splitDatum.split("=")[0]);
        }
        return tags.toArray(new String[0]);
    }

    private String[] getData(String data) {
        String[] splits1 = data.split("\\[");
        if (splits1.length >= 2) {
            String[] splits2 = splits1[1].split("]");

            return splits2[0].split(",");
        }
        return null;
    }

    private boolean isNumber(String string) {
        return string.matches("\\d+");
    }

    private boolean isBoolean(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

}
