package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondBlockCanRandomTick extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondBlockCanRandomTick.class,
                "%blocks/blockdatas/itemtypes% can random[ly] tick",
                "%blocks/blockdatas/itemtypes% (can't|cannot) random[ly] tick")
            .name("Block Can Random Tick")
            .description("Gets if this block is ticked randomly in the world. The blocks current state may change this value.")
            .examples("on right click:",
                "\tif clicked block can random tick:",
                "\t\trandom tick clicked block")
            .since("3.0.0")
            .register();

    }

    private Expression<?> blocks;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(Block.class, "randomTick")) {
            Skript.error("`can random tick` condition requires Paper 1.19+");
            return false;
        }
        this.blocks = exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return this.blocks.check(event, object -> {
            if (object instanceof Block block) return block.getBlockData().isRandomlyTicked();
            else if (object instanceof BlockData blockData) return blockData.isRandomlyTicked();
            else if (object instanceof ItemType itemType) {
                Material material = itemType.getMaterial();
                if (material.isBlock()) return material.createBlockData().isRandomlyTicked();
            }
            return false;
        }, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String can = isNegated() ? " cannot" : " can";
        return this.blocks.toString(e, d) + can + " random tick";
    }

}
