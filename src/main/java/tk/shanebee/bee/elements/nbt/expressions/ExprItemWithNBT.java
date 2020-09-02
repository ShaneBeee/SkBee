package tk.shanebee.bee.elements.nbt.expressions;

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
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;
import tk.shanebee.bee.api.NBTApi.ObjectType;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
@Name("NBT - Item with NBT")
@Description("Give players items with NBT or even use items with NBT in GUIs")
@Examples({"give player diamond sword with nbt \"{Unbreakable:1}\"",
        "format gui slot 1 of player with diamond axe with nbt \"{Enchantments:[{id:\\\"\\\"unbreaking\\\"\\\",lvl:5s}]}\""})
@Since("1.0.0")
public class ExprItemWithNBT extends PropertyExpression<ItemType, ItemType> {

    private static final NBTApi NBT_API;
    static {
        Skript.registerExpression(ExprItemWithNBT.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with [item( |-)]nbt %string%");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings("null")
    private Expression<String> nbt;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        nbt = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected ItemType[] get(Event e, ItemType[] source) {
        String nbt = this.nbt.getSingle(e);
        if (!NBTApi.validateNBT(nbt)) return null;
        return get(source, item -> {
            if (nbt != null)
                NBT_API.addNBT(item, nbt, ObjectType.ITEM_TYPE);
            return item;
        });
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getExpr().toString(e, debug) + " with nbt " + nbt.toString(e, debug);
    }

}
