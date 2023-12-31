package com.shanebeestudios.skbee.elements.structure.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Structure - Save/Delete")
@Description("Save a structure to file (will overwrite if file already exists), or delete a structure file. Requires MC 1.17.1+")
@Examples({"save structure {_s}", "save structures {_s::*}", "delete structure {_s}"})
@Since("1.12.0")
public class EffStructureSave extends Effect {

    static {
        Skript.registerEffect(EffStructureSave.class, "(save|1¦delete) [structure[s] %structures%");
    }

    private Expression<StructureWrapper> structures;
    private boolean save;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.structures = (Expression<StructureWrapper>) exprs[0];
        this.save = parseResult.mark == 0;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (StructureWrapper structureWrapper : this.structures.getAll(event)) {
            if (this.save)
                structureWrapper.save();
            else
                structureWrapper.delete();
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return String.format("%s structure[s] %s", this.save ? "save" : "delete", this.structures.toString(e, d));
    }

}
