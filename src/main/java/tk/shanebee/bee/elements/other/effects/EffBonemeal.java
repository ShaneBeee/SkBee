package tk.shanebee.bee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Bone Meal Effect")
@Description("Apply the bone meal effect to a block.")
@Examples("apply bonemeal effect to target block of player")
@Since("1.7.0")
public class EffBonemeal extends Effect {

    static {
        if (Skript.methodExists(Block.class, "applyBoneMeal", BlockFace.class)) {
            Skript.registerEffect(EffBonemeal.class, "apply bone[ ]meal [effect] to %blocks%");
        }
    }

    private Expression<Block> blocks;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        blocks = (Expression<Block>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        for (Block block : blocks.getArray(e)) {
            block.applyBoneMeal(BlockFace.UP);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "apply bonemeal effect to " + blocks.toString(e, d);
    }

}
