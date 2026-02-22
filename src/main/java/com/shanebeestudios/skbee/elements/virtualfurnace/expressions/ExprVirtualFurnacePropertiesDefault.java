package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.vf.api.property.FurnaceProperties;
import com.shanebeestudios.vf.api.property.Properties;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprVirtualFurnacePropertiesDefault extends SimpleExpression<Properties> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprVirtualFurnacePropertiesDefault.class, Properties.class,
                "default (furnace|1:blast furnace|2:smoker) properties")
            .name("VirtualFurnace - Default Furnace Properties")
            .description("Represents furnace properties that mimic vanilla Minecraft properties.")
            .examples("set {_prop} to default furnace properties",
                "set {_furnace} to virtual furnace named \"Potato\" with properties {_prop}")
            .since("3.3.0")
            .register();
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
