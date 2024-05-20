package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.virtualfurnace.type.Types;
import com.shanebeestudios.vf.api.property.FurnaceProperties;
import com.shanebeestudios.vf.api.property.Properties;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("VirtualFurnace - Furnace Item")
@Description("Get a virtual furnace item. This will be any item with a linked virtual furnace, allowing players " +
        "to access a portable furnace wherever they go. The speed multipliers will allow your furnace item to cook faster " +
        "or burn fuel faster. Ex: 1.0 will burn at normal speed as defined by the recipes, where as 2.0 will burn twice as fast. " +
        "Omitting these values will default to 1.0." +
        "GUI name will be the name that shows up in the furnace GUI")
@Examples("give player a virtual furnace item as diamond named \"MyFurnace\" with gui name \"PORTABLE FURNACE\" with " +
        "cook speed multiplier 1.5")
@Since("1.3.0")
public class ExprVirtualFurnaceItem extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprVirtualFurnaceItem.class, ItemType.class, ExpressionType.COMBINED,
                "[a] [(:glowing)] virtual furnace item as %itemtype% with (inventory|gui) name %string%" +
                        " [[and ]with cook speed multiplier %number%] [[and ]with fuel speed multiplier %number%]",
                "[a] [(:glowing)] virtual furnace item as %itemtype% with (inventory|gui) name %string% ",
                "with [[furnace ]properties] %-machineproperty%");
    }

    private int pattern;
    private Expression<ItemType> itemType;
    private Expression<String> name;
    private Expression<Number> cookSpeed;
    private Expression<Number> fuelSpeed;
    private Expression<Properties> properties;
    private boolean glowing;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parse) {
        this.pattern = matchedPattern;
        this.itemType = (Expression<ItemType>) exprs[0];
        this.name = (Expression<String>) exprs[1];
        if (matchedPattern == 0) {
            this.cookSpeed = (Expression<Number>) exprs[2];
            this.fuelSpeed = (Expression<Number>) exprs[3];
        } else {
            this.properties = (Expression<Properties>) exprs[2];
        }
        this.glowing = parse.hasTag("glowing");
        return true;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    protected ItemType @Nullable [] get(Event event) {
        ItemType itemType = this.itemType.getSingle(event);
        if (itemType == null) return null;
        ItemStack itemStack = itemType.getRandom();

        FurnaceProperties furnaceProperties;
        if (this.pattern == 0) {
            double cookspeed = this.cookSpeed != null ? this.cookSpeed.getSingle(event).doubleValue() : 1.0;
            double fuelSpeed = this.fuelSpeed != null ? this.fuelSpeed.getSingle(event).doubleValue() : 1.0;
            String key = "key_" + itemStack.getType() + "_" + cookspeed + "_" + fuelSpeed;
            furnaceProperties = new FurnaceProperties(key).cookMultiplier(cookspeed).fuelMultiplier(fuelSpeed);
        } else if (this.properties.getSingle(event) instanceof FurnaceProperties p) {
            furnaceProperties = p;
        } else {
            furnaceProperties = FurnaceProperties.FURNACE;
        }
        String name = this.name != null ? this.name.getSingle(event) : "uh-oh";
        ItemStack furnaceItem = Types.FURNACE_MANAGER.createItemWithFurnace(
                name,
                furnaceProperties,
                itemStack,
                glowing);
        return new ItemType[]{new ItemType(furnaceItem)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String properties;
        if (this.pattern == 0) {
            properties = (this.cookSpeed != null ? " with cook speed " + this.cookSpeed.toString(e, d) : "")
                    + (this.fuelSpeed != null ? " with fuel speed " + this.fuelSpeed.toString(e, d) : "");
        } else {
            properties = " with properties " + this.properties.toString(e, d);
        }
        return (glowing ? "glowing " : "") + "virtual furnace item as " + this.itemType.toString(e, d)
                + " with inventory name " + this.name.toString(e, d)
                + properties;
    }

}
