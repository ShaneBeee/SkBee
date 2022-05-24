package com.shanebeestudios.skbee.elements.structure.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.structure.StructureBee;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Structure - Place")
@Description("Place an already created structure into the world. Requires MC 1.17.1+")
@Examples("place structure {_s} at location above target block of player")
@Since("1.12.0")
public class EffStructurePlace extends Effect {

    static {
        if (Skript.classExists("org.bukkit.structure.Structure")) {
            Skript.registerEffect(EffStructurePlace.class,
                    "place [structure] %structure% at %location%");
        }
    }

    private Expression<StructureBee> structure;
    private Expression<Location> location;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        structure = (Expression<StructureBee>) exprs[0];
        location = (Expression<Location>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        StructureBee structure = this.structure.getSingle(event);
        Location location = this.location.getSingle(event);

        if (structure == null || location == null) {
            return;
        }
        structure.place(location);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "paste " + structure.toString(e,d) + " at " + location.toString(e,d);
    }

}
