package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
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
import com.shanebeestudios.skbee.api.util.ObjectConverter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Object from NamespacedKey")
@Description({"Get an object from a namespaced key.",
        "This may come in handy in an instance you have a string version that doesn't match Skript and can't be parsed.",
        "\nCurrently supported types: attribute, biome, damage type, enchantment, entity type, game event, item type, " +
                "particle, potion effect type, statistic, world."})
@Examples({"set {_n} to mc key from \"minecraft:zombie\"",
        "set {_e} to entity type from key {_n}"})
@Since("2.17.0")
public class ExprNamespacedKeyObjectFrom extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprNamespacedKeyObjectFrom.class, Object.class, ExpressionType.COMBINED,
                "%*classinfo% from key[s] %namespacedkeys%");
    }

    private Literal<ClassInfo<?>> classInfo;
    private Expression<NamespacedKey> namespacedKey;
    private ObjectConverter<?> objectConverter;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.classInfo = (Literal<ClassInfo<?>>) exprs[0];
        this.namespacedKey = (Expression<NamespacedKey>) exprs[1];
        this.objectConverter = ObjectConverter.getFromClass(this.classInfo.getSingle().getC());
        if (this.objectConverter == null) {
            Skript.error("SkBee does not have a NamespacedKey converter for '" + this.classInfo.toString() + "'");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object @Nullable [] get(Event event) {
        List<Object> objects = new ArrayList<>();

        for (NamespacedKey key : this.namespacedKey.getArray(event)) {
            objects.add(this.objectConverter.get(key));
        }

        return objects.toArray(new Object[0]);
    }

    @Override
    public boolean isSingle() {
        return this.namespacedKey.isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.classInfo.getSingle().getC();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.classInfo.toString(e, d) + " from key " + this.namespacedKey.toString(e, d);
    }

}
