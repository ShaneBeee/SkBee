package com.shanebeestudios.skbee.elements.generator.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.event.Event;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Name("ChunkGenerator - Populate Tree")
@Description({"Grow a tree in a ChunkGenerator `block pop` section.",
    "The vector represents chunk position not world position."})
@Examples("populate cherry tree at vector(0, 64, 15)")
@Since("INSERT VERSION")
public class EffTreeGenerate extends Effect {

    private static final Random RANDOM = new Random();

    static {
        Skript.registerEffect(EffTreeGenerate.class,
            "populate %bukkittreetype% at %vectors%");
    }

    private Expression<TreeType> treeType;
    private Expression<Vector> vector;

    @SuppressWarnings({"NullableProblems", "unchecked"})
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

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (!(event instanceof BlockPopulateEvent popEvent)) return;

        TreeType treeType = this.treeType.getSingle(event);
        if (treeType == null) return;

        LimitedRegion region = popEvent.getLimitedRegion();
        for (Vector vector : this.vector.getArray(event)) {
            int x = (popEvent.getChunkX() << 4) + MathUtil.clamp(vector.getBlockX(), 0, 15);
            int z = (popEvent.getChunkZ() << 4) + MathUtil.clamp(vector.getBlockZ(), 0, 15);
            Location location = new Location(null, x, vector.getBlockY(), z);
            region.generateTree(location, RANDOM, treeType);

        }

    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "populate " + this.treeType.toString(e, d) + " at " + this.vector.toString(e, d);
    }

}
