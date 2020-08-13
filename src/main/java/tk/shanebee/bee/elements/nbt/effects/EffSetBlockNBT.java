package tk.shanebee.bee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;

@Name("NBT - Set Block with NBT")
@Description("Set a block at a location to a block with NBT. BlockData is also supported when using MC 1.13+ and Skript 2.5+")
@Examples({"set nbt-block at player to west facing furnace with nbt \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&aFurnieFurnace\\\"\"}\"\"}\"",
        "set nbt-block at event-location to hopper with nbt \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&cHoppieHopper\\\"\"}\"\"}\"",
        "set nbt-block at player to furnace[facing=west] with nbt \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&aFurnieFurnace\\\"\"}\"\"}\""})
@Since("1.0.0")
public class EffSetBlockNBT extends Effect {

    private static final NBTApi NBT_API;
    private static final boolean BLOCK_DATA;

    static {
        if (Skript.classExists("org.bukkit.block.data.BlockData") && Skript.classExists("ch.njol.skript.expressions.ExprBlockData")) {
            BLOCK_DATA = true;
            Skript.registerEffect(EffSetBlockNBT.class,
                    "set (nbt[(-| )]block|tile[(-| )]entity) %directions% %locations% to %itemtype/blockdata% with nbt %string%");
        } else {
            BLOCK_DATA = false;
            Skript.registerEffect(EffSetBlockNBT.class,
                    "set (nbt[(-| )]block|tile[(-| )]entity) %directions% %locations% to %itemtype% with nbt %string%");
        }
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Object> type;
    private Expression<String> nbtString;


    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        type = (Expression<Object>) exprs[2];
        locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        nbtString = (Expression<String>) exprs[3];
        return true;
    }

    @Override
    public void execute(final Event e) {
        String value = this.nbtString.getSingle(e);
        if (BLOCK_DATA) {
            final BlockData blockData = ((BlockData) type.getSingle(e));
            if (blockData == null) return;
            for (final Location loc : locations.getArray(e)) {
                assert loc != null : locations;
                Block block = loc.getBlock();
                block.setBlockData(blockData);
                NBT_API.addNBT(block, value);
            }
        } else {
            final ItemType block = ((ItemType) type.getSingle(e));
            if (block == null) return;
            for (final Location loc : locations.getArray(e)) {
                assert loc != null : locations;
                block.getBlock().setBlock(loc.getBlock(), true);
                NBT_API.addNBT(loc.getBlock(), value);
            }
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "set block " + locations.toString(e, debug) + " to " +
                type.toString(e, debug) + " with nbt " + nbtString.toString(e, debug);
    }

}
