package com.shanebeestudios.skbee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.NBT.NBTApi;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
@Name("NBT - Item with NBT")
@Description("Get an item with nbt.")
@Examples({"give player diamond sword with nbt nbt compound from \"{Unbreakable:1}\"",
        "set {_n} to nbt compound from \"{Points:10}\"",
        "set {_i} to netherite axe with nbt {_n}"})
@Since("1.0.0")
public class ExprItemWithNBT extends PropertyExpression<ItemType, ItemType> {

    static {
        Skript.registerExpression(ExprItemWithNBT.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with [item( |-)]nbt %nbtcompound%");
    }

    @SuppressWarnings("null")
    private Expression<Object> nbt;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        nbt = (Expression<Object>) exprs[1];
        return true;
    }

    @Override
    protected ItemType[] get(Event event, ItemType[] source) {
        if (this.nbt.getSingle(event) instanceof NBTCompound nbtCompound) {
            return get(source, itemType -> NBTApi.getItemTypeWithNBT(itemType, nbtCompound));
        }
        return source;
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return getExpr().toString(e, d) + " with nbt " + nbt.toString(e, d);
    }

}
