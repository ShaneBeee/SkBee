package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.vf.api.property.FurnaceProperties;
import com.shanebeestudios.vf.api.property.Properties;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("VirtualFurnace - Furnace Properties Create")
@Description("Create furnace properties to be used in a virtual furnace.")
@Examples("set {_prop} to furnace properties with cook speed multiplier 1.5 and fuel speed multiplier 0.5")
@Since("INSERT VERSION")
public class ExprVirtualFurnacePropertiesCreate extends SimpleExpression<Properties> {

    static {
        Skript.registerExpression(ExprVirtualFurnacePropertiesCreate.class, Properties.class, ExpressionType.COMBINED,
                "[virtual] furnace properties " +
                        "with cook speed multiplier %number% and [with] fuel speed multiplier %number%");
    }

    private Expression<Number> cookX;
    private Expression<Number> fuelX;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.cookX = (Expression<Number>) exprs[0];
        this.fuelX = (Expression<Number>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Properties[] get(Event event) {
        Number cookNum = this.cookX.getSingle(event);
        Number fuelNum = this.fuelX.getSingle(event);

        double cookX = cookNum != null ? cookNum.doubleValue() : 1.0;
        double fuelX = fuelNum != null ? fuelNum.doubleValue() : 1.0;
        String id = "key_machine_" + cookX + "_" + fuelX;
        FurnaceProperties properties = new FurnaceProperties(id)
                .cookMultiplier(cookX)
                .fuelMultiplier(fuelX);
        return new Properties[]{properties};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Properties> getReturnType() {
        return Properties.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String cook = this.cookX.toString(e, d);
        String fuel = this.fuelX.toString(e, d);
        return "furnace properties with cook speed " + cook + " and fuel speed " + fuel;
    }

}
