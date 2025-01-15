package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import com.shanebeestudios.skbee.api.skript.runtime.SyntaxRuntimeErrorProducer;

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

}
