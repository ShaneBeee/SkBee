package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Name("NBT - Save File")
@Description({"Manually save the NBT from a file. When getting the NBT compound from a file, changing values in the compound will",
        "not be automatically applied to the file, and saving will have to be done manually."})
@Examples({"set {_n} to nbt compound from file \"plugins/maScript/some-data.nbt\"",
        "set tag \"ma-tag\" of {_n} to 32",
        "save nbt file of {_n}"})
@Since("INSERT VERSION")
public class EffSaveNBTFile extends Effect {

    static {
        Skript.registerEffect(EffSaveNBTFile.class, "save nbt file[s] (for|of) %nbtcompounds%");
    }

    private Expression<NBTCompound> nbtCompound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.nbtCompound = (Expression<NBTCompound>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event e) {
        for (NBTCompound compound : nbtCompound.getArray(e)) {
            if (compound instanceof NBTFile) {
                try {
                    ((NBTFile) compound).save();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "save nbt file for " + this.nbtCompound.toString(e, d);
    }

}
