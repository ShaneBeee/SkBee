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
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBTApi;

import javax.annotation.Nullable;

@Name("NBT - Item from NBT")
@Description({"This expression allows you to grab an item from NBT strings or compounds (compound support added in INSERT VERSION). ",
        "This can be useful when wanting to grab items from file nbt, or nbt of an entity or an inventory holding block (like a chest or furnace).",
        "It can also be useful for creating your own serializing system. The NBT string required for this must be an item's FULL item NBT ",
        "(This must include the item type and amount, example of full: \"{id:\"minecraft:iron_sword\",tag:{Damage:0},Count:1b}\"",
        " example of partial nbt of the same item: \"{Damage:0}\")."})
@Examples({"set {_i} to item from nbt \"{id:\"\"minecraft:iron_sword\"\",tag:{Damage:0},Count:1b}\"", "\n",
        "set {_nbt::*} to tag \"Inventory\" of file nbt of \"world/playerdata/some-players-uuid.dat\"",
        "loop {_nbt::*}",
        "\tset {_i} to item from nbt loop-value"})
@Since("1.4.10")
public class ExprItemFromNBT extends PropertyExpression<Object, ItemType> {

    private static final NBTApi NBT_API;

    static {
        Skript.registerExpression(ExprItemFromNBT.class, ItemType.class, ExpressionType.PROPERTY,
                "item[s] from nbt[s] %strings/nbtcompounds%",
                "nbt item[s] from %strings/nbtcompounds%");
        NBT_API = SkBee.getPlugin().getNbtApi();
    }

    @SuppressWarnings("null")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean i, @NotNull ParseResult parseResult) {
        setExpr(exprs[0]);
        return true;
    }

    @Override
    protected ItemType @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return get(source, nbt -> {
            if (nbt instanceof String) {
                return NBT_API.getItemTypeFromNBT(((String) nbt));
            } else if (nbt instanceof NBTCompound) {
                return NBT_API.getItemTypeFromNBT(((NBTCompound) nbt));
            }
            return null;
        });
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "Item from nbt string(s) " + getExpr().toString(e, d);
    }

}
