package com.shanebeestudios.skbee.elements.structure.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.structure.StructureManager;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffStructureSave extends Effect {

    private static final StructureManager STRUCTURE_MANAGER = SkBee.getPlugin().getStructureManager();

    public static void register(Registration reg) {
        reg.newEffect(EffStructureSave.class,
                "(save|1:delete) structure[s] %structuretemplates%",
                "(save|1:delete) structure template[s] %structuretemplates%")
            .name("Structure - Save/Delete")
            .description("Save a structure template to file (will overwrite if file already exists), or delete a structure template file.")
            .examples("save structure {_s}",
                "save structures {_s::*}",
                "delete structure {_s}")
            .since("1.12.0")
            .register();
    }

    private Expression<StructureWrapper> structures;
    private boolean save;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.structures = (Expression<StructureWrapper>) exprs[0];
        this.save = parseResult.mark == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (StructureWrapper structureWrapper : this.structures.getArray(event)) {
            if (this.save)
                structureWrapper.save();
            else
                STRUCTURE_MANAGER.deleteStructure(structureWrapper);
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return String.format("%s structure[s] %s", this.save ? "save" : "delete", this.structures.toString(e, d));
    }

}
