package com.shanebeestudios.skbee.elements.structure.effects;

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
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.Location;
import org.bukkit.event.Event;

@Name("Structure - Place")
@Description({"Place an already created structure into the world.",
    "Palette = The palette index of the structure to use, starting at 0, or -1 to pick a random palette.",
    "A palette represents a variation of a structure.",
    "Most structures, like the ones generated with structure blocks, only have a single variant.",
    "Requires MC 1.17.1+"})
@Examples({"set {_s} to structure with id \"minecraft:village/taiga/houses/taiga_cartographer_house_1\"",
    "place structure {_s} above target block of player"})
@Since("1.12.0")
public class EffStructurePlace extends Effect {

    static {
        Skript.registerEffect(EffStructurePlace.class,
            "place [structure] %structure% [using palette %-number%] %directions% %locations%");
    }

    private Expression<StructureWrapper> structure;
    private Expression<Number> palette;
    private Expression<Location> locations;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structure = (Expression<StructureWrapper>) exprs[0];
        this.palette = (Expression<Number>) exprs[1];
        this.locations = Direction.combine((Expression<? extends Direction>) exprs[2], (Expression<? extends Location>) exprs[3]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        StructureWrapper structure = this.structure.getSingle(event);
        int palette = -1;
        if (this.palette != null) {
            Number paletteSingle = this.palette.getSingle(event);
            palette = paletteSingle != null ? paletteSingle.intValue() : -1;
        }

        if (structure == null) return;

        for (Location location : this.locations.getArray(event)) {
            structure.place(location, palette);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(Event e, boolean d) {
        String palette = this.palette != null ? ("using palette " + this.palette.toString(e, d)) : "";
        return "paste " + structure.toString(e, d) + " " + palette +
            locations.toString(e, d);
    }

}
