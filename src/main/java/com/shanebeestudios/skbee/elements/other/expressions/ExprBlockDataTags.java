package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("BlockData - Tags")
@Description("Get a list of all block data tags of a Block or BlockData.")
@Examples({"set {_data::*} to block data tags of target block of player",
        "loop block data tags of target block of player:"})
@Since("1.0.0, INSERT VERSION (BlockData Support)")
public class ExprBlockDataTags extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprBlockDataTags.class, String.class, ExpressionType.PROPERTY,
                "block[ ](data|state) tags of %blocks/blockdatas%");
    }

    private Expression<?> objects;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = exprs[0];
        return true;
    }

    @Override
    protected String @NotNull [] get(@NotNull Event event) {
        List<String> list = new ArrayList<>();
        for (Object object : this.objects.getAll(event)) {
            BlockData blockData;
            if (object instanceof Block block) blockData = block.getBlockData();
            else if (object instanceof BlockData bd) blockData = bd;
            else continue;

            String[] tags = getTags(blockData.getAsString());
            if (tags != null) {
                list.addAll(Arrays.asList(tags));
            }
        }
        return list.toArray(new String[0]);
    }

    @Override
    public @NotNull Class<String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "block data tags of " + this.objects.toString(e, d);
    }

    // Utils
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

}
