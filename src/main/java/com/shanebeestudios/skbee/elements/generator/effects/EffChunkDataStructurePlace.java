package com.shanebeestudios.skbee.elements.generator.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class EffChunkDataStructurePlace extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffChunkDataStructurePlace.class,
                "place chunk[ ]data structure %structure% at %vector%")
            .name("ChunkGenerator - Structure Place")
            .description("Place a structure in a block populator.",
                "Due to the chunk not being finalized yet,",
                "the standard structure place effect will not work during generation.",
                "Since the chunk isn't finalized yet, we use a vector instead of a location,",
                "the vector will represent a chunk position not a world position.")
            .examples("place chunkdata structure {-s} at vector({_x}, {_y}, {_z})",
                "place chunkdata structure {-s} at vector(0,64,0)")
            .since("3.5.0")
            .register();
    }

    private Expression<StructureWrapper> structure;
    private Expression<Vector> vector;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structure = (Expression<StructureWrapper>) exprs[0];
        this.vector = (Expression<Vector>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (!(event instanceof BlockPopulateEvent popEvent)) return;
        StructureWrapper structure = this.structure.getSingle(event);
        Vector vector = this.vector.getSingle(event);
        if (structure == null || vector == null) return;

        vector.setX((popEvent.getChunkX() << 4) + MathUtil.clamp(vector.getBlockX(), 0, 15));
        vector.setZ((popEvent.getChunkZ() << 4) + MathUtil.clamp(vector.getBlockZ(), 0, 15));
        structure.place(popEvent.getLimitedRegion(), vector);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String structure = this.structure.toString(e, d);
        String vector = this.vector.toString(e, d);
        return "place chunkdata structure " + structure + " at " + vector;
    }

}
