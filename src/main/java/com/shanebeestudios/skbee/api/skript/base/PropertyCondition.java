package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import com.shanebeestudios.skbee.api.skript.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's PropertyCondition which implements SyntaxRuntimeErrorProducer
 *
 * @param <T> Type of propety holder
 */
public abstract class PropertyCondition<T> extends ch.njol.skript.conditions.base.PropertyCondition<T> implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
