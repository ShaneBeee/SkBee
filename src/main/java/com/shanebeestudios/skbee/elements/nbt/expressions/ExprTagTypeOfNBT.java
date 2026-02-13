package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTCustomType;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTagTypeOfNBT extends SimpleExpression<NBTCustomType> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprTagTypeOfNBT.class, NBTCustomType.class,
                "[nbt[ ]]tag[ ]type of tag %string% of %nbtcompound%")
            .name("NBT - Tag Type")
            .description("Represents the NBT tag type of a tag in an NBT compound. This is mainly useful for debugging purposes.")
            .examples("set {_type} to tag type of tag \"Pos\" of nbt compound of player")
            .since("1.13.1")
            .register();
    }

    private Expression<String> tag;
    private Expression<NBTCompound> compound;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tag = (Expression<String>) exprs[0];
        this.compound = (Expression<NBTCompound>) exprs[1];
        return true;
    }

    @Override
    protected NBTCustomType @Nullable [] get(Event e) {
        String tag = this.tag.getSingle(e);
        NBTCompound compound = this.compound.getSingle(e);
        if (tag == null || compound == null) {
            return null;
        }

        NBTCustomType tagType = NBTApi.getTagType(compound, tag);
        if (tagType == NBTCustomType.NBTTagEnd) return null;
        return new NBTCustomType[]{tagType};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends NBTCustomType> getReturnType() {
        return NBTCustomType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "nbt tag type of tag " + this.tag.toString(e, d) + " of nbt compound " + this.compound.toString(e, d);
    }

}
