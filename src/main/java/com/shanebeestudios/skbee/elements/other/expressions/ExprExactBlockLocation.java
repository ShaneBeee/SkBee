package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ExprExactBlockLocation extends SimplePropertyExpression<Block, Location> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprExactBlockLocation.class, Location.class, "(exact|true) location", "blocks")
            .name("Block - Exact Location")
            .description("Returns the EXACT location of a block.",
                "Skript's `location of block` adds 0.5 to each x,y,z coord of the location, the \"center\" of the block.",
                "This expression will return the true location of a block.")
            .examples("set {_loc} to exact location of event-block",
                "set {_loc} to exact location of target block",
                "if y coord of exact location of event-block = 5:")
            .since("2.5.1")
            .register();
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
