package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("NBT - Tags")
@Description("Get all tags of an NBT compound.")
@Examples({"set {_t::*} to nbt tags of {_n}", "set {_t::*} to nbt tags of nbt compound of player"})
@Since("INSERT VERSION")
public class ExprTagsOfNBT extends SimpleExpression<String> {

    static {
        PropertyExpression.register(ExprTagsOfNBT.class, String.class, "nbt tags", "nbtcompound");
    }

    private Expression<NBTCompound> compound;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        compound = (Expression<NBTCompound>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected String[] get(Event event) {
        NBTCompound compound = this.compound.getSingle(event);
        if (compound != null) {
            return compound.getKeys().toArray(new String[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "nbt tags of nbt compound " + this.compound.toString(e, d);
    }

}
