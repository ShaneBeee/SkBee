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

import javax.annotation.Nullable;

@Name("NBT - Item from NBT")
@Description({"This expression allows you to grab an item from NBT strings. This can be useful when wanting to grab items ",
        "from file nbt, or nbt of an entity or an inventory holding block (like a chest or furnace).",
        "It can also be useful for creating your own serializing system. The NBT string required for this must be an item's FULL item NBT ",
        "(This must include the item type and amount, example of full: \"{id:\"minecraft:iron_sword\",tag:{Damage:0},Count:1b}\"",
        " example of partial nbt of the same item: \"{Damage:0}\")."})
@Examples({"set {_i} to item from nbt \"{id:\"minecraft:iron_sword\",tag:{Damage:0},Count:1b}\"", "\n",
        "set {_nbt::*} to tag \"Inventory\" of file nbt of \"world/playerdata/some-players-uuid.dat\"",
        "loop {_nbt::*}",
        "\tset {_i} to item from nbt loop-value"})
@Since("INSERT VERSION")
public class ExprItemFromNBT extends PropertyExpression<String, ItemType> {

    private static final NBTApi NBT_API;

    static {
        Skript.registerExpression(ExprItemFromNBT.class, ItemType.class, ExpressionType.PROPERTY,
                "item[s] from nbt[s] %strings%");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean i, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<String>) exprs[0]);
        return true;
    }

    @Override
    protected ItemType[] get(Event e, String[] source) {
        return get(source, NBT_API::getItemTypeFromNBT);
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "Item from nbt string(s) " + getExpr().toString(e, d);
    }

}
