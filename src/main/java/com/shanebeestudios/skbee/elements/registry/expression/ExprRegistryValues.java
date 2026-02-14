package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.registry.RegistryHolder;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;

public class ExprRegistryValues extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprRegistryValues.class, Object.class,
                "registry values of %registrykey%")
            .name("Registry - Registry Values")
            .description("Get all values from a registry.")
            .examples("set {_biomes::*} to registry values of biome registry",
                "loop registry values of item registry:")
            .since("3.8.0")
            .register();
    }

    private Expression<RegistryKey<?>> registryKey;
    private Class<?> returnType = null;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.registryKey = (Expression<RegistryKey<?>>) exprs[0];
        if (this.registryKey instanceof Literal<RegistryKey<?>> literal) {
            this.returnType = RegistryHolders.getRegistryHolder(literal.getSingle()).getReturnType();
        }
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        RegistryKey<?> registryKey = this.registryKey.getSingle(event);
        if (registryKey == null) return null;

        RegistryHolder<?, ?> registryHolder = RegistryHolders.getRegistryHolder(registryKey);
        if (this.returnType == null) {
            this.returnType = registryHolder.getReturnType();
        }
        List<?> values = registryHolder.getValues();
        return values.toArray((Object[]) Array.newInstance(this.returnType, values.size()));
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        if (this.returnType == null) return Object.class;
        return this.returnType;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "registry values of " + this.registryKey.toString(e, d);
    }

}
