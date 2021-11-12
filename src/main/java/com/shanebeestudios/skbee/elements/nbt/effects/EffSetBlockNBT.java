package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

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
                    "set (nbt[(-| )]block|tile[(-| )]entity) %directions% %locations% to %itemtype/blockdata% with nbt %string/nbtcompound%");
        } else {
            BLOCK_DATA = false;
            Skript.registerEffect(EffSetBlockNBT.class,
                    "set (nbt[(-| )]block|tile[(-| )]entity) %directions% %locations% to %itemtype% with nbt %string/nbtcompound%");
        }
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Object> type;
    private Expression<Object> nbtObject;


    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        type = (Expression<Object>) exprs[2];
        locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        nbtObject = (Expression<Object>) exprs[3];
        return true;
    }

    @Override
    public void execute(final @NotNull Event event) {
        Object nbtObject = this.nbtObject.getSingle(event);
        String value = nbtObject instanceof NBTCompound ? nbtObject.toString() : ((String) nbtObject);
        if (value == null) return;
        if (BLOCK_DATA) {
            Object typeObject = type.getSingle(event);
            if (typeObject == null) return;
            for (final Location loc : locations.getArray(event)) {
                assert loc != null : locations;
                Block block = loc.getBlock();
                if (typeObject instanceof BlockData) {
                    block.setBlockData(((BlockData) typeObject));
                } else {
                    ItemType itemType = ((ItemType) typeObject);
                    itemType.setBlock(block, true);
                }
                NBT_API.addNBT(block, value, NBTApi.ObjectType.BLOCK);
            }
        } else {
            final ItemType block = ((ItemType) type.getSingle(event));
            if (block == null) return;
            for (final Location loc : locations.getArray(event)) {
                assert loc != null : locations;
                block.getBlock().setBlock(loc.getBlock(), true);
                NBT_API.addNBT(loc.getBlock(), value, NBTApi.ObjectType.BLOCK);
            }
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "set block " + locations.toString(e, debug) + " to " +
                type.toString(e, debug) + " with nbt " + nbtObject.toString(e, debug);
    }

}
