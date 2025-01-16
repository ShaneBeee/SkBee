package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.config.Node;
import com.shanebeestudios.skbee.api.skript.runtime.SyntaxRuntimeErrorProducer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of Skript's SimplePropertyExpression which implements SyntaxRuntimeErrorProducer
 *
 * @param <F> From type
 * @param <T> To type
 */
public abstract class SimplePropertyExpression<F, T>
    extends ch.njol.skript.expressions.base.SimplePropertyExpression<F, T>
    implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        for (F f : getExpr().getArray(event)) {
            change(f, delta, mode);
        }
    }

    public void change(F from, Object @Nullable [] delta, ChangeMode mode) {
    }

}
