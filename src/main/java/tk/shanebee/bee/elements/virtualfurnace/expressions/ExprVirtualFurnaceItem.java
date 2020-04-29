package tk.shanebee.bee.elements.virtualfurnace.expressions;

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
import com.shanebeestudios.vf.api.VirtualFurnaceAPI;
import com.shanebeestudios.vf.api.property.FurnaceProperties;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Name("VirtualFurnace - Furnace Item")
@Description("Get a virtual furnace item. This will be any item with a linked virtual furnace, allowing players " +
        "to access a portable furnace wherever they go. The speed multipliers will allow your furnace item to cook faster " +
        "or burn fuel faster. Ex: 1.0 will burn at normal speed as defined by the recipes, where as 2.0 will burn twice as fast. " +
        "Omitting these values will default to 1.0." +
        "GUI name will be the name that shows up in the furnace GUI")
@Examples("give player a virtual furnace item as diamond named \"MyFurnace\" with gui name \"PORTABLE FURNACE\" with " +
        "cook speed multiplier 1.5")
@Since("INSERT VERSION")
public class ExprVirtualFurnaceItem extends PropertyExpression<ItemType, ItemType> {

    private static final VirtualFurnaceAPI API = SkBee.getPlugin().getVirtualFurnaceAPI();

    static {
        Skript.registerExpression(ExprVirtualFurnaceItem.class, ItemType.class, ExpressionType.PROPERTY,
                "[a] [(1¦glowing)] virtual furnace item as %itemtype% with (inventory|gui) name %string%" +
                        " [[and ]with cook speed multiplier %number%]" +
                        " [[and ]with fuel speed multiplier %number%]");
    }

    @SuppressWarnings("null")
    private Expression<String> name;
    private Expression<Number> cookSpeed;
    private Expression<Number> fuelSpeed;
    private boolean glowing;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean k, SkriptParser.ParseResult parse) {
        setExpr((Expression<ItemType>) exprs[0]);
        name = (Expression<String>) exprs[1];
        cookSpeed = (Expression<Number>) exprs[2];
        fuelSpeed = (Expression<Number>) exprs[3];
        glowing = parse.mark == 1;
        return true;
    }

    @Override
    protected ItemType[] get(Event event, ItemType[] itemTypes) {
        double cookspeed = this.cookSpeed != null ? this.cookSpeed.getSingle(event).doubleValue() : 1.0;
        double fuelSpeed = this.fuelSpeed != null ? this.fuelSpeed.getSingle(event).doubleValue() : 1.0;
        String name = this.name != null ? this.name.getSingle(event) : "uh-oh";
        String key = "key_" + itemTypes[0].getRandom().getType().toString() + "_" + cookspeed + "_" + fuelSpeed;
        FurnaceProperties prop = new FurnaceProperties(key).cookMultiplier(cookspeed).fuelMultiplier(fuelSpeed);
        return get(itemTypes, itemData -> {
            ItemStack stack = itemData.getRandom();
            if (stack == null || name == null) return null;
            ItemStack i = API.getFurnaceManager().createItemWithFurnace(
                    name,
                    prop,
                    stack,
                    glowing);
            return new ItemType(i);
        });
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "virtual furnace item as " + getExpr().toString(e, d) + " with inventory name " + this.name.toString(e, d)
                + (this.cookSpeed != null ? " with cook speed " + this.cookSpeed.toString(e, d) : "")
                + (this.fuelSpeed != null ? " with fuel speed " + this.fuelSpeed.toString(e, d) : "");
    }

}
