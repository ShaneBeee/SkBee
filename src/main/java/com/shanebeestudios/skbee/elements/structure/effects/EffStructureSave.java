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
import com.shanebeestudios.skbee.api.structure.StructureBee;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Structure - Save")
@Description("Save a structure to file. Will overwrite if already in that file. Requires MC 1.17.1+")
@Examples({"save structure {_s}", "save structures {_s::*}"})
@Since("1.12.0")
public class EffStructureSave extends Effect {

    static {
        if (Skript.classExists("org.bukkit.structure.Structure")) {
            Skript.registerEffect(EffStructureSave.class, "save [structure[s]] %structures%");
        }
    }

    private Expression<StructureBee> structures;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        structures = (Expression<StructureBee>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        for (StructureBee structureBee : this.structures.getAll(e)) {
            structureBee.save();
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "save structure[s] " + this.structures.toString(e,d);
    }

}
