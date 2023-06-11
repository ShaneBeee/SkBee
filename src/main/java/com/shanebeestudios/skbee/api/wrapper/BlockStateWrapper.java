package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.aliases.ItemType;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

/**
 * Wrapper class for {@link BlockState}
 */
@SuppressWarnings("unused")
public class BlockStateWrapper {

    private final BlockState blockState;
    private final boolean fromStructure;

    public BlockStateWrapper(BlockState blockState) {
        this(blockState, false);
    }

    public BlockStateWrapper(BlockState blockState, boolean fromStructure) {
        this.blockState = blockState;
        this.fromStructure = fromStructure;
    }

    /**
     * Get the location of a blockstate as a vector.
     * <p>
     * Represents the offset of a blockstate in a {@link org.bukkit.structure.Structure}
     * </p>
     *
     * @return Offset of block in a structure
     */
    public Vector getOffset() {
        return blockState.getLocation().toVector();
    }

    /**
     * Get the {@link BlockData} of this block state
     *
     * @return BlockData of this block state
     */
    public BlockData getBlockData() {
        return blockState.getBlockData();
    }

    public void setBlockData(BlockData blockData) {
        this.blockState.setBlockData(blockData);
    }

    /**
     * Get an {@link ItemType} from this block state
     *
     * @return ItemType from this block state
     */
    public ItemType getItemType() {
        return new ItemType(blockState);
    }

    public void setItemType(ItemType itemType) {
        this.blockState.setType(itemType.getMaterial());
    }

    /**
     * @see BlockState#update(boolean, boolean)
     */
    public boolean update(boolean force, boolean applyPhysics) {
        return this.blockState.update(force, applyPhysics);
    }

    /**
     * Get the wrapped Bukkit {@link BlockState}
     *
     * @return Bukkit BlockState
     */
    public BlockState getBukkitBlockState() {
        return blockState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockStateWrapper blockStateWrapper) {
            return this.blockState.equals(blockStateWrapper.blockState);
        }
        return false;
    }

    @Override
    public String toString() {
        Location location = this.blockState.getLocation();
        String world = location.getWorld() != null ? location.getWorld().getName() : "huh?";
        String locString = String.format("x:%s,y:%s,z:%s,world:'%s'",
                location.getBlockX(), location.getBlockY(), location.getBlockZ(), world);

        String loc = this.fromStructure ? "offset" : "location";
        String offset = String.valueOf(this.fromStructure ? getOffset() : locString);
        String data = blockState.getBlockData().getAsString();

        return "BlockState{" + loc + "=[" + offset + "],blockdata='" + data + "'}";
    }

}
