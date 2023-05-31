package com.shanebeestudios.skbee.elements.nbt.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("NBT - File Exists")
@Description("Check if an NBT file already exists.")
@Examples("if nbt file \"plugins/MyPlugin/test.nbt\" exists:")
@Since("2.10.0")
public class CondNBTFileExists extends Condition {

    static {
        Skript.registerCondition(CondNBTFileExists.class, "nbt file %string% (exists|1:doesn't exist)");
    }

    private Expression<String> fileName;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.fileName = (Expression<String>) exprs[0];
        setNegated(parseResult.hasTag("1"));
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        return this.fileName.check(event, NBTApi::nbtFileExists, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String exists = isNegated() ? " doesn't exist" : " exists";
        return "nbt file " + this.fileName.toString(e, d) + exists;
    }

}
