package tk.shanebee.bee.elements.nbt.expressions;


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
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.api.util.Util;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Name("NBT - Tag")
@Description({"Returns the value of the specified tag of the specified NBT. Also supports getting nested tags using a semi colon as a delimiter. ",
        "If the return value is a list, you can use it as a list, as it will automatically split it for ya. ",
        "As of 1.6.0 you can also set/delete tags, but only with NBT compounds, not NBT strings. Do note that setting the tag of an item ",
        "will not update it, you will have to create a variable for the NBT compound of the item, set the tag of that variable, then set the item back ",
        "using the 'nbt item of' expression. I understand this is a bit convoluted but unfortunately that is just how it works. See examples. ",
        "Also note that you can NOT create custom tags for entities/blocks."})
@Examples({"set {_tag} to tag \"Invulnerable\" of targeted entity's nbt",
        "send \"Tag: %tag \"\"CustomName\"\" of nbt of target entity%\" to player",
        "set {_tag} to \"Enchantments\" tag of nbt of player's tool",
        "delete tag \"CustomTag\" of {_nbt}",
        "set {_tag} to \"BlockEntityTag;Items\" tag of nbt of target block", "",
        "set {_n} to nbt compound of player's tool",
        "set tag \"tag;Damage\" of {_n} to 500",
        "set player's tool to nbt item of {_n}"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    private static final NBTApi NBT_API;
    private static final boolean DEBUG;

    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.SIMPLE,
                "tag %string% of %string/nbtcompound%", "%string% tag of %string/nbtcompound%");
        NBT_API = SkBee.getPlugin().getNbtApi();
        DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;
    }

    private Expression<String> tag;
    private Expression<Object> nbt;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.tag = (Expression<String>) exprs[0];
        this.nbt = (Expression<Object>) exprs[1];
        return true;
    }

    @Override
    @Nullable
    protected Object[] get(@NotNull Event e) {
        String t = tag.getSingle(e);
        Object object = nbt.getSingle(e);
        String n = object instanceof NBTCompound ? object.toString() : (String) object;
        assert t != null;
        if (t.contains(";")) {
            return getNested(t, n);
        }
        Object nbt = NBT_API.getTag(t, n);
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

            NBT_API.setTag(tag, compound, delta);
        } else if (mode == ChangeMode.DELETE) {
            NBT_API.deleteTag(tag, compound);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "Tag \"" + tag.toString(e, d) + "\" of " + nbt.toString(e, d);
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    private Object[] getNested(String tag, String nbt) {
        if (nbt == null) return null;
        String[] split = tag.split(";");
        Object nbtNew = nbt;
        for (String s : split) {
            NBTContainer container = new NBTContainer(nbtNew.toString());
            nbtNew = NBT_API.getTag(s, container.toString()); // TODO api for this
            if (nbtNew == null) {
                if (DEBUG) {
                    Util.skriptError("Invalid tag \"&b" + s + "&7\" in &b" + container.toString());
                }
                return null;
            }
        }
        if (nbtNew instanceof ArrayList) {
            return ((ArrayList<?>) nbtNew).toArray();
        }
        return new Object[]{nbtNew};
    }

}
