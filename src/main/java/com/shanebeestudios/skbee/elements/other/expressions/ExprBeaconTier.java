package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

@Name("Beacon - Tier")
@Description("Returns the current tier of a beacon block.")
@Examples({"on right click on a beacon:",
        "\tsend \"The current tier of this block is %beacon tier of event-block%\""})
@Since("2.16.0")
public class ExprBeaconTier extends SimplePropertyExpression<Block, Integer> {

    static {
        register(ExprBeaconTier.class, Integer.class, "beacon tier", "blocks");
    }

    @Override
    @Nullable
    public Integer convert(Block block) {
        if (block.getState() instanceof Beacon beacon)
            return beacon.getTier();
        return null;
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "beacon tier";
    }

}
