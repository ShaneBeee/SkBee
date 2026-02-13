package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.registration.Registration;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffSetBlockNBT extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSetBlockNBT.class,
                "set (nbt[(-| )]block|tile[(-| )]entity) %directions% %locations% to %itemtype/blockdata% with [nbt] %nbtcompound%")
            .name("NBT - Set Block with NBT")
            .description("Set a block at a location to a block with NBT. BlockData is also supported when using MC 1.13+ and Skript 2.5+")
            .examples(
                "set {_n} to nbt compound from \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&aFurnieFurnace\\\"\"}\"\"}\"",
                "set nbt-block at player to west facing furnace with nbt {_n}",
                "set nbt-block at player to furnace[facing=west] with nbt {_n}",
                "set {_n} to nbt compound from \"{CustomName:\"\"{\\\"\"text\\\"\":\\\"\"&cHoppieHopper\\\"\"}\"\"}\"",
                "set nbt-block at event-location to hopper with nbt {_n}",
                "set {_nbt} to nbt compound from \"{custom:{BlockOwner:\"%uuid of player%\"}}\"",
                "set nbt-block at player to coal ore with nbt {_n}")
            .since("1.0.0")
            .register();
    }

    @SuppressWarnings("null")
    private Expression<Location> locations;
    private Expression<Object> type;
    private Expression<NBTCompound> nbt;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        type = (Expression<Object>) exprs[2];
        locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        nbt = (Expression<NBTCompound>) exprs[3];
        return true;
    }

    @Override
    public void execute(final @NotNull Event event) {
        NBTCompound compound = this.nbt.getSingle(event);
        if (compound == null) return;

        Object typeObject = type.getSingle(event);
        if (typeObject == null) return;
        for (final Location loc : locations.getArray(event)) {
            assert loc != null : locations;
            Block block = loc.getBlock();
            if (typeObject instanceof BlockData blockData) {
                block.setBlockData(blockData);
            } else {
                ItemType itemType = ((ItemType) typeObject);
                itemType.setBlock(block, true);
            }
            NBTApi.addNBTToBlock(block, compound);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "set block " + locations.toString(e, debug) + " to " +
                type.toString(e, debug) + " with nbt " + nbt.toString(e, debug);
    }

}
