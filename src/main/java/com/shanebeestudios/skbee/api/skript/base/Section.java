package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import com.shanebeestudios.skbee.api.skript.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's Section which implements SyntaxRuntimeErrorProducer
 */
public abstract class Section extends ch.njol.skript.lang.Section implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
