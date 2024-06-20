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
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Name("NamespacedKey - Object From")
@Description({"Get an object from a namespaced key.",
    "This may come in handy in an instance you have a string version that doesn't match Skript and can't be parsed.",
    "Currently supported types: attribute, biome, damage type, enchantment, entity type, game event, item type,",
    "particle, potion effect type, statistic, world."})
@Examples({"set {_n} to mc key from \"minecraft:zombie\"",
    "set {_e} to entity type from key {_n}",
    "set {_e} to entity type from key \"armadillo\"",
    "set {_e} to entity type from key \"minecraft:breeze\"",
    "spawn 1 of {_e} above target block",
    "",
    "set {_i} to itemtype from key \"minecraft:stone_sword\"",
    "give player 1 of {_i}",
    "",
    "set {_e} to enchantment from key \"minecraft:breach\"",
    "set {_e} to enchantment from key \"custom:my_custom_enchant\"",
    "set enchantment level of {_e} of player's tool to 3"})
@Since("2.17.0")
public class ExprNamespacedKeyObjectFrom extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprNamespacedKeyObjectFrom.class, Object.class, ExpressionType.COMBINED,
            "%*classinfo% from key[s] %namespacedkeys/strings%");
    }

    private Literal<ClassInfo<?>> classInfo;
    private Expression<?> namespacedKey;
    private ObjectConverter<?> objectConverter;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.classInfo = (Literal<ClassInfo<?>>) exprs[0];
        this.namespacedKey = exprs[1];
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

        for (Object object : this.namespacedKey.getArray(event)) {
            if (object instanceof NamespacedKey key) {
                objects.add(this.objectConverter.get(key));
            } else if (object instanceof String string) {
                NamespacedKey key = Util.getMCNamespacedKey(string, false);
                objects.add(this.objectConverter.get(key));
            }
        }
        // Prevent ClassCastException (taken from Skript's parsed as expression)
        Object[] objectArray = (Object[]) Array.newInstance(this.classInfo.getSingle().getC(), objects.size());
        for (int i = 0; i < objectArray.length; i++) {
            objectArray[i] = objects.get(i);
        }
        return objectArray;
    }

    @Override
    public boolean isSingle() {
        return this.namespacedKey.isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.classInfo.getSingle().getC();
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return this.classInfo.toString(e, d) + " from key " + this.namespacedKey.toString(e, d);
    }

}
