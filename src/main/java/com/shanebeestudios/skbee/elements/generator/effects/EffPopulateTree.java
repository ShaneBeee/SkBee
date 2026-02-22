package com.shanebeestudios.skbee.elements.generator.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.event.Event;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EffPopulateTree extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffPopulateTree.class,
                "populate %bukkittreetype% at %vectors%")
            .name("ChunkGenerator - Populate Tree")
            .description("Grow a tree in a ChunkGenerator `block pop` section.",
                "The vector represents chunk position not world position.")
            .examples("populate cherry tree at vector(0, 64, 15)")
            .since("3.5.3")
            .register();
    }

    private Expression<TreeType> treeType;
    private Expression<Vector> vector;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.treeType = (Expression<TreeType>) exprs[0];
        this.vector = (Expression<Vector>) exprs[1];
        if (!ParserInstance.get().isCurrentEvent(BlockPopulateEvent.class)) {
            Skript.error("Grow effect using a vector will only work in a 'block pop' section.");
            return false;
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (!(event instanceof BlockPopulateEvent popEvent)) return;

        TreeType treeType = this.treeType.getSingle(event);
        if (treeType == null) {
            return;
        }

        LimitedRegion region = popEvent.getLimitedRegion();
        Random random = popEvent.getRandom();
        for (Vector vector : this.vector.getArray(event)) {
            int x = (popEvent.getChunkX() << 4) + MathUtil.clamp(vector.getBlockX(), 0, 15);
            int z = (popEvent.getChunkZ() << 4) + MathUtil.clamp(vector.getBlockZ(), 0, 15);
            Location location = new Location(null, x, vector.getBlockY(), z);
            region.generateTree(location, random, treeType);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "populate " + this.treeType.toString(e, d) + " at " + this.vector.toString(e, d);
    }

}
