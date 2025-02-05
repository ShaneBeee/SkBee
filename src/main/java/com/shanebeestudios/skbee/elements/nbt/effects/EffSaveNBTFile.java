package com.shanebeestudios.skbee.elements.nbt.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTFile;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Name("NBT - Save File")
@Description({"Manually save the NBT from a file. When getting the NBT compound from a file, changing values in the compound will",
    "not be automatically applied to the file, and saving will have to be done manually."})
@Examples({"set {_n} to nbt compound from file \"plugins/maScript/some-data.nbt\"",
    "set tag \"ma-tag\" of {_n} to 32",
    "save nbt file of {_n}"})
@Since("1.14.0")
public class EffSaveNBTFile extends Effect {

    public static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;

    static {
        Skript.registerEffect(EffSaveNBTFile.class, "save nbt file[s] (from|for|of) %nbtcompounds%");
    }

    private Expression<NBTCompound> nbtCompound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.nbtCompound = (Expression<NBTCompound>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (NBTCompound compound : nbtCompound.getArray(event)) {
            if (compound instanceof NBTFile nbtFile) {
                try {
                    nbtFile.save();
                } catch (IOException ex) {
                    if (DEBUG) {
                        throw new SkriptAPIException(ex.getMessage(), ex);
                    } else {
                        error("Could not save file: '" + nbtFile.getName() + "', got error '" + ex.getMessage() + "'. Enable debug in SkBee config for more detailed error.");
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "save nbt file for " + this.nbtCompound.toString(e, d);
    }

}
