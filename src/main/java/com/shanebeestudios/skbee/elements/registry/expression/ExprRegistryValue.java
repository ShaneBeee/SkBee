package com.shanebeestudios.skbee.elements.registry.expression;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.Utils;
import com.shanebeestudios.skbee.api.registry.RegistryHolder;
import com.shanebeestudios.skbee.api.registry.RegistryHolders;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.config.SkBeeMetrics;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ExprRegistryValue extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprRegistryValue.class, Object.class,
                "value of %string/typedkey/namespacedkey% (in|from) %registrykey%",
                "%registrykey% value [of] %string/typedkey/namespacedkey%")
            .name("Registry - Registry Value")
            .description("Get the value of a registry entry by key.")
            .examples("set {_v} to value of \"minecraft:plains\" in biome registry",
                "set {_v} to value of \"minecraft:diamond_sword\" from item registry",
                "set {_v} to block registry value of \"minecraft:stone\"")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<?> key;
    private Expression<RegistryKey<?>> registryKey;
    private Class<?> returnType = null;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        SkBeeMetrics.Features.REGISTRY.used();
        this.key = expressions[matchedPattern];
        this.registryKey = (Expression<RegistryKey<?>>) expressions[matchedPattern == 0 ? 1 : 0];
        if (this.registryKey instanceof Literal<RegistryKey<?>> literal) {
            this.returnType = RegistryHolders.getRegistryHolder(literal.getSingle()).getReturnType();
        }
        return true;
    }

    @SuppressWarnings({"ReassignedVariable", "unchecked", "rawtypes"})
    @Override
    protected Object @Nullable [] get(Event event) {
        RegistryKey<?> registryKey = this.registryKey.getSingle(event);
        if (registryKey == null) return null;

        RegistryHolder<?, ?> registryHolder = RegistryHolders.getRegistryHolder(registryKey);
        if (this.returnType == null) {
            this.returnType = registryHolder.getReturnType();
        }

        TypedKey<?> typedKey = null;
        Object o = this.key.getSingle(event);
        if (o instanceof TypedKey<?> tk) {
            typedKey = tk;
        } else if (o instanceof String string) {
            NamespacedKey namespacedKey = Utils.getNamespacedKey(string, false);
            if (namespacedKey != null) {
                typedKey = TypedKey.create(registryKey, namespacedKey);
            }
        } else if (o instanceof NamespacedKey nsk) {
            typedKey = TypedKey.create(registryKey, nsk);
        }
        if (typedKey == null) return null;

        List<Object> values = new ArrayList<>();
        values.add(registryHolder.getValue((TypedKey) typedKey));

        return values.toArray((Object[]) Array.newInstance(this.returnType, values.size()));

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        if (this.returnType == null) return Object.class;
        return this.returnType;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "value of " + this.key.toString(event, debug) + " from " + this.registryKey.toString(event, debug);
    }

}
