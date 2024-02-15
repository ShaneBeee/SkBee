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

@Name("VirtualFurnace - Default Furnace Properties")
@Description("Represents furnace properties that mimic vanilla Minecraft properties.")
@Examples({"set {_prop} to default furnace properties",
        "set {_furnace} to virtual furnace named \"Potato\" with properties {_prop}"})
@Since("3.3.0")
public class ExprVirtualFurnacePropertiesDefault extends SimpleExpression<Properties> {

    static {
        Skript.registerExpression(ExprVirtualFurnacePropertiesDefault.class, Properties.class, ExpressionType.SIMPLE,
                "default (furnace|1:blast furnace|2:smoker) properties");
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Properties[] get(Event event) {
        FurnaceProperties properties = switch (this.pattern) {
            case 1 -> FurnaceProperties.BLAST_FURNACE;
            case 2 -> FurnaceProperties.SMOKER;
            default -> FurnaceProperties.FURNACE;
        };
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

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String properties = switch (this.pattern) {
            case 1 -> "blast furnace";
            case 2 -> "smoker";
            default -> "furnace";
        };
        return "default " + properties + " properties";
    }

}
