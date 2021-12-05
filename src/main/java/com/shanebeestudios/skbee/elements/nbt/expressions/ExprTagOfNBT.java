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
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import com.shanebeestudios.skbee.api.NBT.NBTCustom;
import com.shanebeestudios.skbee.api.NBT.NBTCustomTileEntity;
import com.shanebeestudios.skbee.api.NBT.NBTCustomType;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Name("NBT - Tag")
@Description({"Returns the value of the specified tag of the specified NBT. Also supports getting nested tags using a semi colon as a delimiter.",
        "If the return value is a list, you can use it as a list, as it will automatically split it for ya.",
        "As of 1.6.0 you can also set/delete tags, but only with NBT compounds, not NBT strings. Do note that setting the tag of an item",
        "will not update it, you will have to create a variable for the NBT compound of the item, set the tag of that variable, then set the item back",
        "using the 'nbt item of' expression. I understand this is a bit convoluted but unfortunately that is just how it works. See examples.",
        "Also note that you can NOT create custom tags for entities/blocks. As of 1.7.1 blocks(tile entities)/entities will be able to hold custom nbt",
        "data in the \"custom\" tag of said block/entity. Due to Minecraft not supporting this, I had to use some hacky methods to make this happen.",
        "That said, this system is a tad convoluted, see the SkBee WIKI for more details.",
        "As of 1.10.0 you can now add custom NBT to any block (the same as you would for tile entities)(This is only support on 1.16.4+). ",
        "Since Minecraft does not natively support this, the NBT is actually stored in the chunk's NBT. See wiki for more info.",
        "As of 1.10.0 you can get/set specific tag types of NBT compounds, allowing for more detailed control of what type your tag is going to be."})
@Examples({"set {_tag} to tag \"Invulnerable\" of targeted entity's nbt",
        "send \"Tag: %tag \"\"CustomName\"\" of nbt of target entity%\" to player",
        "set {_tag} to \"Enchantments\" tag of nbt of player's tool",
        "delete tag \"CustomTag\" of {_nbt}",
        "set {_tag} to \"BlockEntityTag;Items\" tag of nbt of target block", "",
        "set {_n} to nbt compound of player's tool",
        "set tag \"tag;Damage\" of {_n} to 500",
        "set player's tool to nbt item of {_n}", "",
        "set byte tag \"points\" of {_nbt} to 1",
        "set int tag \"custom;score\" of nbt compound of player to 10",
        "set {_i} to int tag \"Score\" of nbt compound of player",
        "set {_t::*} to compound list tag \"abilities\" of nbt compound of player"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    private static final NBTApi NBT_API;
    private static final boolean HAS_PERSISTENCE;

    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.COMBINED,
                "tag %string% of %string/nbtcompound%",
                "%string% tag of %string/nbtcompound%",
                "%nbttype% %string% of %nbtcompound%",
                "%string% %nbttype% of %nbtcompound%");
        NBT_API = SkBee.getPlugin().getNbtApi();
        HAS_PERSISTENCE = Skript.isRunningMinecraft(1, 14);
    }

    private Expression<String> tag;
    private Expression<Object> nbt;
    @Nullable
    private Expression<NBTCustomType> nbtType;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.tag = (Expression<String>) exprs[matchedPattern == 2 ? 1 : 0];
        this.nbt = (Expression<Object>) exprs[matchedPattern < 2 ? 1 : 2];
        this.nbtType = matchedPattern > 1 ? (Expression<NBTCustomType>) exprs[matchedPattern == 2 ? 0 : 1] : null;
        return true;
    }

    @Override
    @Nullable
    protected Object[] get(@NotNull Event e) {
        String t = tag.getSingle(e);
        Object object = nbt.getSingle(e);
        NBTCustomType type = this.nbtType != null ? this.nbtType.getSingle(e) : null;
        if (object == null) {
            return null;
        }
        NBTCompound n = object instanceof NBTCompound ? ((NBTCompound) object) : new NBTContainer((String) object);
        assert t != null;
        Object nbt = type != null ? NBT_API.getTag(t, n, type) : NBT_API.getTag(t, n);
        if (nbt instanceof ArrayList) {
            return ((ArrayList<?>) nbt).toArray();
        }
        return new Object[]{nbt};
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
            return CollectionUtils.array(Object[].class);
        }
        return null;
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = this.nbt.getSingle(e);
        if (!(object instanceof NBTCompound)) return;
        String tag = this.tag.getSingle(e);
        NBTCompound compound = ((NBTCompound) object);
        if (tag == null) return;

        if (mode == ChangeMode.SET) {
            if (delta == null) return;

            if (this.nbtType != null) {
                NBTCustomType type = this.nbtType.getSingle(e);
                NBT_API.setTag(tag, compound, delta, type);
            } else {
                NBT_API.setTag(tag, compound, delta);
            }
            if (compound instanceof NBTCustomTileEntity) {
                ((NBTCustomTileEntity) compound).updateBlockstate();
            }
        } else if (mode == ChangeMode.DELETE) {
            if (HAS_PERSISTENCE && tag.equalsIgnoreCase("custom")) {
                if (compound instanceof NBTCustom) {
                    ((NBTCustom) compound).deleteCustomNBT();
                }
            }
            NBT_API.deleteTag(tag, compound);
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
