package com.shanebeestudios.skbee.api.skript.base;

import ch.njol.skript.config.Node;
import com.shanebeestudios.skbee.api.skript.runtime.SyntaxRuntimeErrorProducer;

/**
 * Extension of Skript's EffectSection which implements SyntaxRuntimeErrorProducer
 */
public abstract class EffectSection extends ch.njol.skript.lang.EffectSection implements SyntaxRuntimeErrorProducer {

    Node node = getParser().getNode();

    @Override
    public Node getNode() {
        return this.node;
    }

}
