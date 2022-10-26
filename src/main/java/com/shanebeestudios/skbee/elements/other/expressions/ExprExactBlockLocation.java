package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Block - Exact Location")
@Description({"Returns the EXACT location of a block.",
        "Skript's `location of block` adds 0.5 to each x,y,z coord of the location, the \"center\" of the block.",
        "This expression will return the true location of a block."})
@Examples({"set {_loc} to exact location of event-block",
        "set {_loc} to exact location of target block",
        "if y coord of exact location of event-block = 5:"})
@Since("INSERT VERSION")
public class ExprExactBlockLocation extends SimplePropertyExpression<Block, Location> {

    static {
        register(ExprExactBlockLocation.class, Location.class, "(exact|true) location", "blocks");
    }

    @Override
    public @Nullable Location convert(Block block) {
        return block.getLocation();
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "exact location";
    }

}
