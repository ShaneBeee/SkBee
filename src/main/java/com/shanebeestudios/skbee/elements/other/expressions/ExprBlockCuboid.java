package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Version;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Name("Block Cuboid")
@Description("All the blocks within a cuboid located between 2 location points")
@Examples({"set {_blocks::*} to all blocks within {_loc1} and {_loc2}",
        "set all blocks within {_loc1} and {_loc2} to stone",
        "loop all blocks within {_loc1} and {_loc2}:", "\tif loop-block is stone:", "\t\tset loop-block to grass"})
@Since("1.0.0")
public class ExprBlockCuboid extends SimpleExpression<Block> {

    static {
        // Skript added this expression in 2.5.1, so we don't need to register it
        if (Skript.getVersion().isSmallerThan(new Version(2, 5, 1))) {
            Skript.registerExpression(ExprBlockCuboid.class, Block.class, ExpressionType.COMBINED,
                    "[(all [[of] the]|the)] blocks within %location% and %location%");
        }
    }

    private Expression<Location> from;
    private Expression<Location> to;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        from = (Expression<Location>) exprs[0];
        to = (Expression<Location>) exprs[1];
        return true;
    }

    @Override
    protected Block[] get(Event e) {
        Location from = this.from != null ? this.from.getSingle(e) : null;
        Location to = this.to != null ? this.to.getSingle(e) : null;
        if (to == null || from == null) return null;
        ArrayList<Block> list = new ArrayList<>(getBlocks(from, to));
        return list.toArray(new Block[0]);
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "cuboid blocks within " + from.toString(e, d) + " and " + to.toString(e, d);
    }

    private List<Block> getBlocks(Location loc1, Location loc2) {

        World w = loc1.getWorld();
        List<Block> blocks = new ArrayList<>();

        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();

        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();

        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;

        if (x1 > x2) {
            xMin = x2;
            xMax = x1;
        } else {
            xMin = x1;
            xMax = x2;
        }

        if (y1 > y2) {
            yMin = y2;
            yMax = y1;
        } else {
            yMin = y1;
            yMax = y2;
        }

        if (z1 > z2) {
            zMin = z2;
            zMax = z1;
        } else {
            zMin = z1;
            zMax = z2;
        }

        for (x = xMin; x <= xMax; x++) {
            for (y = yMin; y <= yMax; y++) {
                for (z = zMin; z <= zMax; z++) {
                    Block b = new Location(w, x, y, z).getBlock();
                    blocks.add(b);
                }
            }
        }
        return blocks;
    }

}
