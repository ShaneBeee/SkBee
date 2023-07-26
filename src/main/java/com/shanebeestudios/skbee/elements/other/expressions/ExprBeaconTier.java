package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.beacon.BeaconTier;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

@Name("Beacon - Tier")
@Description("Returns the current tier of a beacon block.")
@Examples({"on right click on a beacon:",
        "\tsend \"The current tier of this block is %beacon tier of event-block%\""})
@Since("INSERT VERSION")
public class ExprBeaconTier extends SimplePropertyExpression<Block, BeaconTier> {

    static {
        register(ExprBeaconTier.class, BeaconTier.class, "beacon tier", "blocks");
    }

    @Override
    @Nullable
    public BeaconTier convert(Block block) {
        if (block.getState() instanceof Beacon beacon)
            return BeaconTier.getTierFromInt(beacon.getTier());
        return null;
    }

    @Override
    public Class<? extends BeaconTier> getReturnType() {
        return BeaconTier.class;
    }

    @Override
    protected String getPropertyName() {
        return "beacon tier";
    }

}
