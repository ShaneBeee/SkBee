package tk.shanebee.bee.api.structure;

import ch.njol.skript.aliases.ItemType;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

/**
 * Wrapper class for {@link BlockState}
 */
public class BlockStateBee {

    private final BlockState blockState;

    public BlockStateBee(BlockState blockState) {
        this.blockState = blockState;
    }

    /** Get the offset of a blockstate in a {@link org.bukkit.structure.Structure}
     *
     * @return Offset of block in a structure
     */
    public Vector getOffset() {
        return blockState.getLocation().toVector();
    }

    public BlockData getBlockData() {
        return blockState.getBlockData();
    }

    public ItemType getItemType() {
        return new ItemType(blockState);
    }

    public BlockState getBukkitBlockState() {
        return blockState;
    }

    @Override
    public String toString() {
        Vector offset = getOffset();
        String data = blockState.getBlockData().getAsString();
        return "BlockState{" +
                "offset=[" + offset + "],blockdata='" + data + "'}";
    }

}
