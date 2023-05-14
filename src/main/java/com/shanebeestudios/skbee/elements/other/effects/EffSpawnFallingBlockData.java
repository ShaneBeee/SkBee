package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Spawn Falling BlockData")
@Description({"Spawn a falling block data.",
        "This is a temp effect until Skript properly handles block data with spawning of falling blocks."})
@Examples("le spawn falling snow[layers=3] above target block of player")
@Since("INSERT VERSION")
public class EffSpawnFallingBlockData extends Effect {

    static {
        Skript.registerEffect(EffSpawnFallingBlockData.class,
                "(skbee|le) spawn falling %blockdata% [%directions% %locations%]");
    }

    private Expression<BlockData> blockData;
    private Expression<Location> locations;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.blockData = (Expression<BlockData>) exprs[0];
        this.locations = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BlockData blockData = this.blockData.getSingle(event);
        if (blockData == null) return;

        for (Location location : this.locations.getArray(event)) {
            World world = location.getWorld();
            FallingBlock fallingBlock = world.spawnFallingBlock(location, blockData);
            SkriptUtils.setLastSpawned(fallingBlock);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "le spawn falling " + this.blockData.toString(e, d) + this.locations.toString(e, d);
    }

}
