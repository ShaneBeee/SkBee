package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's PropertyExpression which implements SyntaxRuntimeErrorProducer
 *
 * @param <F> From type
 * @param <T> To type
 */
public abstract class PropertyExpression<F, T> extends ch.njol.skript.expressions.base.PropertyExpression<F, T>
    implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
