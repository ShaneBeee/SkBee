package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registry.RegistryHolder;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;

@Name("Registry - Registry Values")
@Description("Get all values from a registry.")
@Examples({"set {_biomes::*} to registry values of biome registry",
    "loop registry values of item registry:"})
@Since("3.8.0")
public class ExprRegistryValues extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprRegistryValues.class, Object.class, ExpressionType.COMBINED,
            "registry values of %registrykey%");
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
