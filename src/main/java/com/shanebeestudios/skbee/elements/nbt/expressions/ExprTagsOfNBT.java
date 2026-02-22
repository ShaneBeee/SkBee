package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Locale;

public class ExprTagsOfNBT extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprTagsOfNBT.class, String.class, "nbt tags", "nbtcompound")
            .name("NBT - Tags")
            .description("Get all tags of an NBT compound.")
            .examples(
                "set {_t::*} to nbt tags of {_n}",
                "set {_t::*} to nbt tags of nbt compound of player"
            )
            .since("1.14.2")
            .register();
    }

    private Expression<NBTCompound> compound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.compound = (Expression<NBTCompound>) exprs[0];
        return true;
    }

    @Override
    protected String[] get(Event event) {
        NBTCompound compound = this.compound.getSingle(event);
        if (compound != null) {
            return compound.getKeys().stream().sorted(Comparator.comparing(
                key -> key.toLowerCase(Locale.ROOT))).toArray(String[]::new);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "nbt tags of nbt compound " + this.compound.toString(e, d);
    }

}
