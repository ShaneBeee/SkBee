package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.NBT.NBTCustomTileEntity;
import com.shanebeestudios.skbee.api.NBT.NBTCustomType;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Name("NBT - Tag")
@Description({"Get/set/delete the value of the specified tag of an NBT compound. Also supports getting nested tags using a semi colon as a delimiter.",
        "If the return value is a list, you can use it as a list, as it will automatically split it for ya.",
        "Note: Entities/blocks can not natively hold custom NBT tags. SkBee allows you to put custom nbt",
        "data in the \"custom\" tag of a block/entity's NBT compound. Due to Minecraft not supporting this, I had to use some hacky methods to make this happen.",
        "That said, this system is a tad convoluted, see the SkBee WIKI for more details."})
@Examples({"set {_tag} to tag \"Invulnerable\" of nbt compound of target entity",
        "send \"Tag: %tag \"\"CustomName\"\" of nbt compound of target entity%\" to player",
        "set {_tag::*} to compound list tag \"Enchantments\" of nbt item compound of player's tool",
        "delete tag \"CustomTag\" of {_nbt}",
        "set {_tag} to \"BlockEntityTag;Items\" tag of nbt compound of target block", "",
        "set {_n} to nbt compound of player's tool",
        "set tag \"tag;Damage\" of {_n} to 500",
        "set player's tool to nbt item of {_n}", "",
        "set byte tag \"points\" of {_nbt} to 1",
        "set int tag \"custom;score\" of nbt compound of player to 10",
        "set {_i} to int tag \"Score\" of nbt compound of player",
        "set {_t::*} to compound list tag \"abilities\" of nbt compound of player",
        "delete tag \"Enchantments\" of nbt item compound of player's tool"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.COMBINED,
                "tag %string% of %nbtcompound%",
                "%string% tag of %nbtcompound%",
                "%nbttype% %string% of %nbtcompound%",
                "%string% %nbttype% of %nbtcompound%");
    }

    private Expression<String> tag;
    private Expression<NBTCompound> nbt;
    @Nullable
    private Expression<NBTCustomType> nbtType;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.tag = (Expression<String>) exprs[matchedPattern == 2 ? 1 : 0];
        this.nbt = (Expression<NBTCompound>) exprs[matchedPattern < 2 ? 1 : 2];
        this.nbtType = matchedPattern > 1 ? (Expression<NBTCustomType>) exprs[matchedPattern == 2 ? 0 : 1] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(@NotNull Event event) {
        String tag = this.tag.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);

        NBTCustomType type = this.nbtType != null ? this.nbtType.getSingle(event) : null;
        assert tag != null;

        Object object = type != null ? NBTApi.getTag(tag, nbt, type) : NBTApi.getTag(tag, nbt);
        if (object instanceof ArrayList arrayList) {
            return arrayList.toArray();
        }
        return new Object[]{object};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Object[].class);
        }
        return null;
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        NBTCompound compound = this.nbt.getSingle(e);
        String tag = this.tag.getSingle(e);
        if (compound == null || tag == null) return;

        if (mode == ChangeMode.SET) {
            if (delta == null) return;

            if (this.nbtType != null) {
                NBTCustomType type = this.nbtType.getSingle(e);
                NBTApi.setTag(tag, compound, delta, type);
            } else {
                NBTApi.setTag(tag, compound, delta);
            }
            if (compound instanceof NBTCustomTileEntity) {
                ((NBTCustomTileEntity) compound).updateBlockstate();
            }
        } else if (mode == ChangeMode.DELETE) {
            NBTApi.deleteTag(tag, compound);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = this.nbtType != null ? this.nbtType.toString(e, d) : "tag";
        String tag = this.tag.toString(e, d);
        String nbt = this.nbt.toString(e, d);
        return String.format("%s %s of %s", type, tag, nbt);
    }

}
