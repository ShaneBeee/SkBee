package com.shanebeestudios.skbee.elements.other.expressions;

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
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("NamespacedKey - Get")
@Description({"NamespacedKeys are a way to declare and specify game objects in Minecraft,",
        "which can identify built-in and user-defined objects without potential ambiguity or conflicts.",
        "NamespacedKeys are a string based key which consists of two components - a namespace and a key.",
        "\nNamespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens.",
        "Minecraft generally uses the \"minecraft\" namespace for built in objects.",
        "\nIf a namespace is not provided, the SkBee config namespace will be used by default -> \"skbee:your_key\"",
        "\nKeys may only contain lowercase alphanumeric characters, periods, underscores, hyphens, and forward slashes.",
        "\nKeep an eye on your console when using namespaced keys as errors will spit out when they're invalid."})
@Examples({"set {_n} to namespaced key from \"minecraft:log\"",
        "set {_custom} to namespaced key from \"my_server:custom_log\"",
        "set {_n} to namespaced key from \"le_test\""})
@Since("2.6.0")
public class ExprNamespacedKey extends SimpleExpression<NamespacedKey> {

    static {
        Skript.registerExpression(ExprNamespacedKey.class, NamespacedKey.class, ExpressionType.COMBINED,
                "[mc:(minecraft|mc)] (namespaced|resource)[ ](key|id[entifier]|location) from %strings%");
    }

    private Expression<String> strings;
    private boolean useMinecraftNamespace;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.strings = (Expression<String>) exprs[0];
        this.useMinecraftNamespace = parseResult.hasTag("mc");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable NamespacedKey[] get(Event event) {
        List<NamespacedKey> namespacedKeys = new ArrayList<>();
        if (useMinecraftNamespace) {
            for (String string : this.strings.getArray(event)) {
                namespacedKeys.add(Util.getMCNamespacedKey(string, true));
            }
        } else {
            for (String string : this.strings.getArray(event)) {
                namespacedKeys.add(Util.getNamespacedKey(string, true));
            }
        }
        return namespacedKeys.toArray(new NamespacedKey[0]);
    }

    @Override
    public boolean isSingle() {
        return this.strings.isSingle();
    }

    @Override
    public @NotNull Class<? extends NamespacedKey> getReturnType() {
        return NamespacedKey.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "namespaced key from '" + this.strings.toString(event, debug) + "'";
    }

}
