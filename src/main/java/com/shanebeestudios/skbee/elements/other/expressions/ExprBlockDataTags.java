package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.BlockDataUtils;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExprBlockDataTags extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBlockDataTags.class, String.class,
                "block[ ](data|state) tags", "blocks/blockdatas")
            .name("BlockData - Tags")
            .description("Get a list of all block data tags of a Block or BlockData.")
            .examples("set {_data::*} to block data tags of target block of player",
                "loop block data tags of target block of player:")
            .since("1.0.0, 2.16.1 (BlockData Support)")
            .register();
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

            String[] tags = BlockDataUtils.getBlockDataTags(blockData);
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

}
