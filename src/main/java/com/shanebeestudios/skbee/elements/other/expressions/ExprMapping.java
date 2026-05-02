package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ExprMapping extends SimpleExpression<Object> {

    private static final Map<String,Object> MAP = new HashMap<>();

    // A lil somethin somethin for myself for testing
    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprMapping.class, Object.class,
            "mapped value %string%")
            .noDoc()
            .register();
    }

    private Expression<String> key;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        String key = this.key.getSingle(event);
        return new Object[]{MAP.get(key)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1) return;

        String key = this.key.getSingle(event);
        MAP.put(key, delta[0]);
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "";
    }

}
