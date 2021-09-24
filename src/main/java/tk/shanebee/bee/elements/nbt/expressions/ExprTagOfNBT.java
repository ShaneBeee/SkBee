package tk.shanebee.bee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.ExprArithmetic;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Comparators;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTType;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBT.NBTCustom;
import tk.shanebee.bee.api.NBT.NBTCustomType;
import tk.shanebee.bee.api.NBTApi;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

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
public class ExprTagOfNBT<T> extends SimpleExpression<T> {

    private static final NBTApi NBT_API;
    private static final boolean HAS_PERSISTENCE;

    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.PROPERTY,
                "tag %string% of %string/nbtcompound%",
                "%string% tag of %string/nbtcompound%",
                "%nbttype% %string% of %string/nbtcompound%",
                "%string% %nbttype% of %string/nbtcompound%",
                "[all] tags of %string/nbtcompound%");
        NBT_API = SkBee.getPlugin().getNbtApi();
        HAS_PERSISTENCE = Skript.isRunningMinecraft(1, 14);
    }

    public ExprTagOfNBT() {
        this(null, (Class<? extends T>) Object.class);
    }

    public ExprTagOfNBT(ExprTagOfNBT<?> source, Class<? extends T>... types) {
        this.source = source;
        if (source != null) {
            this.nbt = source.nbt;
            this.tag = source.tag;
            this.nbtType = source.nbtType;
            this.isAllTags = source.isAllTags;
            this.notSingle = source.notSingle;
            this.hasType = source.hasType;
            this.literalString = source.literalString;
        }
        this.types = types;
        this.superType = (Class<T>) Utils.getSuperType(types);
    }

    private final ExprTagOfNBT<?> source;
    private final Class<? extends T>[] types;
    private final Class<T> superType;

    private Expression<?> nbt;
    @Nullable
    private Expression<String> tag;
    @Nullable
    private NBTCustomType nbtType;
    private boolean isAllTags, notSingle, hasType, literalString;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.isAllTags = matchedPattern == 4;
        this.nbt = exprs[matchedPattern < 2 ? 1 : this.isAllTags ? 0 : 2];
        this.tag = this.isAllTags ? null : (Expression<String>) exprs[matchedPattern == 2 ? 1 : 0];
        boolean canInnit = true;
        if (matchedPattern > 1 && !this.isAllTags) {
            Expression<?> nbtTypeExpr = exprs[matchedPattern == 3 ? 1 : 0];
            canInnit = nbtTypeExpr instanceof Literal;
            if (canInnit)
                this.nbtType = ((Literal<NBTCustomType>) nbtTypeExpr).getSingle();
        }
        this.literalString = this.nbt instanceof VariableString;
        this.hasType = this.nbtType != null;
        this.notSingle = isAllTags || (this.hasType && Stream.of("Array", "List").anyMatch(this.nbtType.name()::contains));
        return canInnit;
    }

    @Override
    @Nullable
    protected T[] get(Event e) {
        Object object = nbt.getSingle(e);
        NBTCompound n = object instanceof NBTCompound ? ((NBTCompound) object) : object != null && NBTApi.validateNBT((String) object) ? new NBTContainer((String) object) : null;
        if (n == null) return null;
        if (isAllTags)
            return convert((Object[]) n.getKeys().toArray(new String[0]));
        else {
            String t = this.tag.getSingle(e);
            if (t == null) return null;
            Object nbt = hasType ? NBT_API.getTag(t, n, nbtType) : NBT_API.getTag(t, n);
            if (nbt == null) return null;
            if (nbt instanceof ArrayList) {
                ArrayList<?> arr = (ArrayList<?>) nbt;
                return convert(!hasType ? arr.toArray() : arr.toArray((Object[]) Array.newInstance(fromNBTType(), 0)));
            }
            if (hasType) {
                Object[] toArr = (Object[]) Array.newInstance(fromNBTType(), 1);
                toArr[0] = nbt;
                return convert(toArr);
            }
            return convert(nbt);
        }
    }


    private T[] convert(Object... objects) {
        try {
            return Converters.convertArray(objects, types, superType);
        } catch (Exception ex) {
            return (T[]) Array.newInstance(superType, 0);
        }
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (((mode != ChangeMode.ADD && mode != ChangeMode.REMOVE) || !hasType || notSingle || fromNBTType().isAssignableFrom(Number.class)) && ((mode != ChangeMode.RESET && mode != ChangeMode.REMOVE_ALL) || !hasType || notSingle) && !(isAllTags || literalString))
            if (hasType)
                return CollectionUtils.array(notSingle ? Array.newInstance(fromNBTType(),0).getClass() : fromNBTType());
            else
                return CollectionUtils.array(Boolean[].class, String[].class, Number[].class, NBTCompound[].class);
        return null;
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Object object = nbt.getSingle(e);
        if (object == null && mode != ChangeMode.DELETE) return;
        String tag = this.tag.getSingle(e);
        if (tag == null) return;
        if (nbt instanceof ExprObjectNBT) {
            ExprObjectNBT nbtExpr = ((ExprObjectNBT) nbt);
            NBTCompound compound = new NBTContainer(object != null ? object.toString() : "{}");
            editNBT(e, compound, tag, mode, delta);
            nbtExpr.change(e, CollectionUtils.array(compound), ChangeMode.SET);
        } else if (object instanceof NBTCompound) {
            editNBT(e, (NBTCompound) object, tag, mode, delta);
        }
    }

    private void editNBT(Event e, NBTCompound compound, String tag, ChangeMode mode, Object... delta) {
        boolean hasDelta = (delta != null && delta.length != 0);
        switch (mode) {
            case SET:
                if (hasDelta) {
                    if (hasType)
                        NBT_API.setTag(tag, compound, delta, nbtType);
                    else {
                        compound.removeKey(tag);
                        NBT_API.setTag(tag, compound, delta);
                    }
                } else if (notSingle || compound.getType(tag) == NBTType.NBTTagList || !compound.hasKey(tag)) {
                    compound.removeKey(tag);
                    NBTCompoundList compoundList = compound.getCompoundList(tag);
                    compoundList.addCompound();
                    compoundList.clear();
                }
                return;
            case ADD:
                Object toAdd = hasType ? NBT_API.getTag(tag, compound, nbtType) : NBT_API.getTag(tag, compound);
                if (notSingle || toAdd instanceof ArrayList) {
                    NBTCustomType type = NBTCustomType.getByTag(compound, tag);
                    if (hasType && !this.nbtType.equals(type))
                        return;
                    this.hasType = true;
                    this.nbtType = type;
                    Object[] listItems = toAdd instanceof ArrayList ? ((ArrayList<?>) toAdd).toArray() : null;
                    editNBT(e, compound, tag, ChangeMode.SET, hasDelta ? ArrayUtils.addAll(listItems, delta) : listItems);
                } else if (hasDelta && delta[0] instanceof Number)
                    editNBT(e, compound, tag, ChangeMode.SET, doMath(e, toAdd instanceof Number ? (Number) toAdd : 0, (Number) delta[0], 0));
                return;
            case REMOVE:
            case REMOVE_ALL:
                Object toRemove = hasType ? NBT_API.getTag(tag, compound, nbtType) : NBT_API.getTag(tag, compound);
                if (notSingle || toRemove instanceof ArrayList) {
                    NBTCustomType type = NBTCustomType.getByTag(compound, tag);
                    if (hasType && !this.nbtType.equals(type))
                        return;
                    this.hasType = true;
                    this.nbtType = type;
                    Object[] newDelta = null;
                    if (toRemove instanceof ArrayList) {
                        ArrayList<Object> list = new ArrayList<>((ArrayList<?>) toRemove);
                        if (hasDelta)
                            Arrays.stream(delta)
                                    .map(o -> o instanceof Number ? new SkriptNumber((Number) o) : o)
                                    .forEach(mode == ChangeMode.REMOVE ? list::remove : o -> {while (list.remove(o));});
                        newDelta = list.toArray();
                    }
                    editNBT(e, compound, tag, ChangeMode.SET, newDelta);
                } else if (hasDelta && delta[0] instanceof Number)
                    editNBT(e, compound, tag, ChangeMode.SET, doMath(e, toRemove instanceof Number ? (Number) toRemove : 0, (Number) delta[0], 1));
                return;
            case DELETE:
                if (HAS_PERSISTENCE && tag.equalsIgnoreCase("custom")) {
                    if (compound instanceof NBTCustom) {
                        ((NBTCustom) compound).deleteCustomNBT();
                    }
                }
                NBT_API.deleteTag(tag, compound);
                return;
            case RESET:
                editNBT(e, compound, tag, ChangeMode.SET);
        }
    }

    @Override
    public boolean isSingle() {
        return !notSingle;
    }

    private Class<?> fromNBTType() {
        switch (this.nbtType) {
            case NBTTagString:
            case NBTTagStringList:
                return String.class;
            case NBTTagCompound:
            case NBTTagCompoundList:
                return NBTCompound.class;
        }
        return Number.class;
    }

    @Override
    public @NotNull Class<? extends T> getReturnType() {
        return isAllTags ? (Class<? extends T>) String.class : this.nbtType != null ? (Class<? extends T>) fromNBTType() : superType;
    }

    @Nullable
    @Override
    public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
        return isAllTags || hasType ? super.getConvertedExpression(to) : new ExprTagOfNBT<>(this, to);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (isAllTags)
            return "all tags of " + nbt.toString(e, d);
        String type = this.nbtType != null ? this.nbtType.getName() : "tag";
        String tag = this.tag.toString(e, d);
        String nbt = this.nbt.toString(e, d);
        return String.format("%s %s of %s", type, tag, nbt);
    }

    @Override
    public Expression<?> getSource() {
        return source == null ? this : source;
    }


    private Number doMath(Event e, Number n1, Number n2, int op) {
        ExprArithmetic arithmetic = new ExprArithmetic();
        arithmetic.init(CollectionUtils.array(new SimpleLiteral<>(n1, false), new SimpleLiteral<>(n2, false)), op, null, null);
        return arithmetic.getSingle(e);
    }

    private static class SkriptNumber {
        private static final Comparator<? super Number, ? super Number> numberCompare = Comparators.getComparator(Number.class, Number.class);
        private final Number n;
        public SkriptNumber(Number n) {
            this.n = n;
        }
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Number && numberCompare.compare(n, (Number) obj) == Comparator.Relation.EQUAL;
        }
    }

}
