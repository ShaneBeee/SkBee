package com.shanebeestudios.skbee.elements.virtualfurnace.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.virtualfurnace.type.Types;
import com.shanebeestudios.vf.api.machine.Furnace;
import com.shanebeestudios.vf.api.property.FurnaceProperties;
import com.shanebeestudios.vf.api.property.Properties;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprVirtualFurnaceCreate extends SimpleExpression<Furnace> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprVirtualFurnaceCreate.class, Furnace.class,
                "virtual furnace (named|with name) %string% [with [[furnace ]properties] %-machineproperty%]")
            .name("VirtualFurnace - Create")
            .description("Create a virtual furnace. When properties are not defined, default vanilla Minecraft properties are used.")
            .examples("set {_furnace} to virtual furnace named \"Le Furnace\"",
                "",
                "set {_prop} to default furnace properties",
                "set {_furnace} to virtual furnace named \"Potato\" with properties {_prop}")
            .since("3.3.0")
            .register();
    }

    private Expression<String> name;
    private Expression<Properties> properties;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        this.properties = (Expression<Properties>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Furnace @Nullable [] get(Event event) {
        String name = this.name.getSingle(event);
        if (name == null) return null;

        Properties properties = FurnaceProperties.FURNACE; // Default properties
        if (this.properties != null) properties = this.properties.getSingle(event);

        // Might support other machines/properties in the future
        if (properties instanceof FurnaceProperties furnaceProperties) {
            Furnace furnace = Types.FURNACE_MANAGER.createFurnace(name, furnaceProperties);
            return new Furnace[]{furnace};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Furnace> getReturnType() {
        return Furnace.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String name = this.name.toString(e, d);
        String properties = this.properties != null ? " with properties " + this.properties.toString(e, d) : "";
        return "virtual furnace named " + name + properties;
    }

}
