package com.shanebeestudios.skbee.elements.virtualfurnace.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.vf.api.RecipeManager;
import com.shanebeestudios.vf.api.recipe.Fuel;
import com.shanebeestudios.vf.api.util.Util;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import com.shanebeestudios.skbee.SkBee;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
@Name("VirtualFurnace - Furnace Fuel")
@Description("Register recipes for fuels for virtual furnaces. Burn time will determine how long this fuel will burn for." +
        "Alternatively you can use the all vanilla fuel effect, and it will register all fuels that match vanilla fuels." +
        "Due to a limitation in Minecraft, this only accepts items which can be used as fuels in vanilla furnaces.")
@Examples({"on load:",
        "\tregister furnace fuel coal with burn time 8 minutes",
        "on load:",
        "\tregister all vanilla fuels"})
@Since("1.3.0")
public class EffFurnaceFuel extends Effect {

    private static final RecipeManager RECIPE_MANAGER = SkBee.getPlugin().getVirtualFurnaceAPI().getRecipeManager();

    static {
        Skript.registerEffect(EffFurnaceFuel.class,
                "register furnace fuel %itemtype% with burn time %timespan%",
                "register all vanilla [furnace] fuels");
    }

    private Expression<ItemType> fuel;
    private Expression<Timespan> burn;
    private boolean vanilla;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        vanilla = pattern == 1;
        if (!vanilla) {
            fuel = (Expression<ItemType>) exprs[0];
            burn = (Expression<Timespan>) exprs[1];
        }
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (vanilla) {
            for (Fuel fuel : Fuel.getVanillaFuels()) {
                RECIPE_MANAGER.registerFuel(fuel);
            }
        } else {
            Material fuel = this.fuel.getSingle(e).getMaterial();
            int burn = (int) this.burn.getSingle(e).getAs(Timespan.TimePeriod.TICK);
            String key = "fuel_" + fuel.toString() + "_" + burn;
            Fuel f = new Fuel(Util.getKey(key), fuel, burn);
            RECIPE_MANAGER.registerFuel(f);
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        if (vanilla) {
            return "register all vanilla furnace fuels";
        } else {
            return "register furnace fuel for " + this.fuel.toString(e, d) + " with burn time " + this.burn.toString(e, d);
        }
    }

}
