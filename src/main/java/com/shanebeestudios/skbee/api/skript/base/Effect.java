package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's Effect which implements SyntaxRuntimeErrorProducer
 */
public abstract class Effect extends ch.njol.skript.lang.Effect implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
