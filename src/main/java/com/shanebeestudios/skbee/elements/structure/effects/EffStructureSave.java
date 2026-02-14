package com.shanebeestudios.skbee.elements.structure.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffStructureSave extends Effect {

    private static final StructureManager STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();

    public static void register(Registration reg) {
        reg.newEffect(EffStructureSave.class, "(save|1:delete) structure[s] %structures%")
            .name("Structure - Save/Delete")
            .description("Save a structure to file (will overwrite if file already exists), or delete a structure file.")
            .examples("save structure {_s}",
                "save structures {_s::*}",
                "delete structure {_s}")
            .since("1.12.0")
            .register();
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
        for (StructureWrapper structureWrapper : this.structures.getArray(event)) {
            if (this.save)
                structureWrapper.save();
            else
                STRUCTURE_MANAGER.deleteStructure(structureWrapper);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return String.format("%s structure[s] %s", this.save ? "save" : "delete", this.structures.toString(e, d));
    }

}
