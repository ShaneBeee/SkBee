package com.shanebeestudios.skbee.elements.generator.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Name("ChunkGenerator - Structure Place")
@Description({"Place a structure in a block populator.",
        "Due to the chunk not being finalized yet,",
        "the standard structure place effect will not work during generation.",
        "Since the chunk isn't finalized yet, we use a vector instead of a location,",
        "but it's treated the same as a location."})
@Examples("place chunkdata structure {-s} at vector({_x}, {_y}, {_z})")
@Since("3.5.0")
public class EffChunkDataStructurePlace extends Effect {

    static {
        Skript.registerEffect(EffChunkDataStructurePlace.class,
                "place chunk[ ]data structure %structure% at %vector%");
    }

    private Expression<StructureWrapper> structure;
    private Expression<Vector> vector;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structure = (Expression<StructureWrapper>) exprs[0];
        this.vector = (Expression<Vector>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (!(event instanceof BlockPopulateEvent populateEvent)) return;
        StructureWrapper structure = this.structure.getSingle(event);
        Vector vector = this.vector.getSingle(event);
        if (structure == null || vector == null) return;

        structure.place(populateEvent.getLimitedRegion(), vector);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String structure = this.structure.toString(e, d);
        String vector = this.vector.toString(e, d);
        return "place chunkdata structure " + structure + " at " + vector;
    }

}
