package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("NBT - Empty Compound")
@Description("Returns an empty/new NBT compound.")
@Examples({"set {_n} to blank nbt compound",
        "set tag \"points\" of {_n} to 10"})
@Since("2.8.0")
public class ExprBlankNBTCompound extends SimpleExpression<NBTCompound> {

    static {
        Skript.registerExpression(ExprBlankNBTCompound.class, NBTCompound.class, ExpressionType.SIMPLE,
                "[a[n]] (blank|empty|new) nbt compound");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable NBTCompound[] get(Event event) {
        return new NBTCompound[]{new NBTContainer()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends NBTCompound> getReturnType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "blank nbt compound";
    }

}
