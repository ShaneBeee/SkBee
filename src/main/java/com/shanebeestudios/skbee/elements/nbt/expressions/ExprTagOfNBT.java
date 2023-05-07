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

import javax.annotation.Nullable;
import java.util.ArrayList;

@Name("NBT - Tag")
@Description({"Get/set/delete the value of the specified tag of an NBT compound. Also supports getting nested tags using a semi colon as a delimiter.",
        "If the return value is a list, you can use it as a list, as it will automatically split it for ya.",
        "\nNOTE: `uuid tag` will set an int array tag (This is how MC stores uuids). On return it'll convert back to a uuid.",
        "\nNOTE: Entities/blocks can not natively hold custom NBT tags. SkBee allows you to put custom nbt",
        "data in the \"custom\" tag of a block/entity's NBT compound. Due to Minecraft not supporting this, I had to use some hacky methods to make this happen.",
        "That said, this system is a tad convoluted, see the SkBee WIKI for more details.",
        "\nADD: You can add numbers to number type tags, you can also add numbers/strings/compounds to lists type tags.",
        "\nREMOVE: You can remove numbers from number type tags, you can also remove numbers/strings from lists type tags.",
        "(You can NOT remove compounds from lists type tags)"})
@Examples({"set {_tag} to tag \"Invulnerable\" of nbt compound of target entity",
        "send \"Tag: %tag \"CustomName\" of nbt compound of target entity%\" to player",
        "set {_tag::*} to compound list tag \"Enchantments\" of nbt compound of player's tool",
        "delete tag \"CustomTag\" of {_nbt}",
        "set {_tag} to \"BlockEntityTag;Items\" tag of nbt compound of target block", "",
        "set {_n} to nbt compound of player's tool",
        "set tag \"tag;Damage\" of {_n} to 500",
        "set byte tag \"points\" of {_n} to 1", "",
        "set int tag \"custom;score\" of nbt compound of player to 10",
        "set {_i} to int tag \"Score\" of nbt compound of player",
        "set {_t::*} to compound list tag \"abilities\" of nbt compound of player",
        "delete tag \"Enchantments\" of nbt item compound of player's tool",
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
                "tag %string% of %nbtcompound%",
                "%string% tag of %nbtcompound%",
                "%nbttype% %string% of %nbtcompound%",
                "%string% %nbttype% of %nbtcompound%");
    }

    private Expression<String> tag;
    private Expression<NBTCompound> nbt;
    @Nullable
    private Literal<NBTCustomType> nbtTypeLit;
    @Nullable
    private Expression<NBTCustomType> nbtType;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        this.tag = (Expression<String>) exprs[matchedPattern == 2 ? 1 : 0];
        this.nbt = (Expression<NBTCompound>) exprs[matchedPattern < 2 ? 1 : 2];
        if (matchedPattern > 1) {
            Expression<?> expr = exprs[matchedPattern == 2 ? 0 : 1];
            if (expr instanceof Literal<?>) {
                this.nbtTypeLit = (Literal<NBTCustomType>) expr;
            }
            this.nbtType = (Expression<NBTCustomType>) expr;
        }
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
        if (object instanceof ArrayList<?> arrayList) {
            return arrayList.toArray();
        }
        return new Object[]{object};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            if (this.nbtType == null) {
                Skript.error("add/remove is only supported when using '%nbttype% tag'!");
                return null;
            } else if (this.nbtTypeLit == null) {
                Skript.error("NBT TYPE must be a literal, variables are not accepted!");
                return null;
            } else {
                NBTCustomType nbtType = this.nbtTypeLit.getSingle();
                if (nbtType == NBTCustomType.NBTTagCompoundList && mode == ChangeMode.REMOVE) {
                    Skript.error("NBT compounds cannot be removed from an NBT compound list!");
                    return null;
                }
                if (nbtType.isList() || nbtType.getTypeClass() == Number.class) {
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

    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        NBTCompound compound = this.nbt.getSingle(event);
        String tag = this.tag.getSingle(event);
        if (compound == null || tag == null) return;

        if (mode == ChangeMode.DELETE) {
            NBTApi.deleteTag(tag, compound);
            return;
        }
        if (delta == null) return;

        if (mode == ChangeMode.SET) {
            if (this.nbtType != null) {
                NBTCustomType type = this.nbtType.getSingle(event);
                NBTApi.setTag(tag, compound, delta, type);
            } else {
                NBTApi.setTag(tag, compound, delta);
            }
        } else if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            if (this.nbtType == null) return;
            NBTCustomType type = this.nbtType.getSingle(event);
            if (mode == ChangeMode.ADD) {
                NBTApi.addToTag(tag, compound, delta, type);
            } else {
                NBTApi.removeFromTag(tag, compound, delta, type);
            }
        }
    }

    @Override
    public boolean isSingle() {
        if (this.nbtTypeLit != null) return !this.nbtTypeLit.getSingle().isList();
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (this.nbtTypeLit != null) return this.nbtTypeLit.getSingle().getTypeClass();
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
