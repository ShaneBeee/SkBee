package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's SimpleExpression which implements SyntaxRuntimeErrorProducer
 *
 * @param <T> To type
 */
public abstract class SimpleExpression<T> extends ch.njol.skript.lang.util.SimpleExpression<T> implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
