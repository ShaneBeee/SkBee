package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTCustomType;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Name("NBT - Tag")
@Description({"Get/set/delete the value of the specified tag of an NBT compound. Also supports getting nested tags using a semi colon as a delimiter.",
        "If the return value is a list, you can use it as a list, as it will automatically split it for ya.",
        "\nNOTE: `uuid tag` will set an int array tag (This is how MC stores uuids). On return it'll convert back to a uuid.",
        "\nNOTE: Entities/blocks can not natively hold custom NBT tags. SkBee allows you to put custom nbt",
        "data in the \"custom\" compound tag of a block/entity's NBT compound. Due to Minecraft not supporting this, I had to use some hacky methods to make this happen.",
        "That said, this system is a tad convoluted, see the SkBee WIKI for more details.",
        "\nADD: You can add numbers to number type tags, you can also add numbers/strings/compounds to lists type tags.",
        "\nREMOVE: You can remove numbers from number type tags, you can also remove numbers/strings from lists type tags.",
        "(You can NOT remove compounds from lists type tags)"})
@Examples({"set {_tag} to byte tag \"Invulnerable\" of nbt of target entity",
        "send \"Tag: %string tag \"CustomName\" of nbt of target entity%\" to player",
        "set {_tag::*} to compound list tag \"Enchantments\" of nbt of player's tool",
        "delete string tag \"CustomTag\" of {_nbt}",
        "",
        "set {_n} to nbt compound of player's tool",
        "set int tag \"Damage\" of {_n} to 500",
        "set byte tag \"custom;points\" of {_n} to 1",
        "",
        "set int tag \"custom;score\" of nbt of player to 10",
        "set {_i} to int tag \"Score\" of nbt of player",
        "set {_t::*} to compound list tag \"abilities\" of nbt of player",
        "delete compound list tag \"Enchantments\" of nbt of player's tool",
        "add 5 to int tag \"points\" of {_n}",
        "remove 5 from int tag \"points\" of {_n}",
        "add \"bob\" and \"joe\" to string list tag \"names\" of {_n}",
        "remove \"bob\" from string list tag \"names\" of {_n}",
        "add 1,2,3 to byte array tag \"bytes\" of {_n}",
        "remove 1 and 2 from byte array tag \"bytes\" of {_n}"})
@Since("1.0.0")
public class ExprTagOfNBT extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprTagOfNBT.class, Object.class, ExpressionType.COMBINED,
                "%nbttype% %string% of %nbtcompound%");
    }

    @Nullable
    private Literal<NBTCustomType> nbtTypeLit;
    private Expression<NBTCustomType> nbtType;
    private Expression<String> tag;
    private Expression<NBTCompound> nbt;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        if (exprs[0] instanceof Literal<?>) {
            this.nbtTypeLit = (Literal<NBTCustomType>) exprs[0];
        }
        this.nbtType = (Expression<NBTCustomType>) exprs[0];
        this.tag = (Expression<String>) exprs[1];
        this.nbt = (Expression<NBTCompound>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object @Nullable [] get(@NotNull Event event) {
        NBTCustomType type = this.nbtType.getSingle(event);
        String tag = this.tag.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);

        if (type == null || tag == null || nbt == null) return null;

        Object object = NBTApi.getTag(tag, nbt, type);
        if (object instanceof ArrayList<?> arrayList) {
            return arrayList.toArray();
        }
        return new Object[]{object};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            if (this.nbtTypeLit == null) {
                Skript.error("NBT TYPE must be a literal, variables are not accepted!");
                return null;
            } else {
                NBTCustomType nbtType = this.nbtTypeLit.getSingle();
                if (nbtType == NBTCustomType.NBTTagCompoundList && mode == ChangeMode.REMOVE) {
                    Skript.error("NBT compounds cannot be removed from an NBT compound list!");
                    return null;
                }
                if (nbtType.getTypeClass().isArray() || nbtType.getTypeClass() == Number.class) {
                    return CollectionUtils.array(nbtType.getTypeClass());
                }
            }
        } else if (mode == ChangeMode.SET) {
            if (this.nbtTypeLit != null) {
                NBTCustomType nbtType = this.nbtTypeLit.getSingle();
                if (nbtType != NBTCustomType.NBTTagUUID) {
                    return CollectionUtils.array(nbtType.getTypeClass());
                }
            }
            return CollectionUtils.array(Object[].class);
        } else if (mode == ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        NBTCustomType type = this.nbtType.getSingle(event);
        String tag = this.tag.getSingle(event);
        NBTCompound nbt = this.nbt.getSingle(event);

        if (type == null || tag == null || nbt == null) return;

        if (mode == ChangeMode.DELETE) {
            NBTApi.deleteTag(tag, nbt);
            return;
        }
        if (delta == null) return;

        if (mode == ChangeMode.SET) {
            NBTApi.setTag(tag, nbt, delta, type);
        } else if (mode == ChangeMode.ADD) {
            NBTApi.addToTag(tag, nbt, delta, type);
        } else if (mode == ChangeMode.REMOVE) {
            NBTApi.removeFromTag(tag, nbt, delta, type);
        }
    }

    @Override
    public boolean isSingle() {
        if (this.nbtTypeLit != null) return !this.nbtTypeLit.getSingle().getTypeClass().isArray();
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (this.nbtTypeLit != null) {
            Class<?> nbtTypeClass = this.nbtTypeLit.getSingle().getTypeClass();
            if (nbtTypeClass.getComponentType() != null) return nbtTypeClass.getComponentType();
            return nbtTypeClass;
        }
        return Object.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = this.nbtType.toString(e, d);
        String tag = this.tag.toString(e, d);
        String nbt = this.nbt.toString(e, d);
        return String.format("%s %s of %s", type, tag, nbt);
    }

}
