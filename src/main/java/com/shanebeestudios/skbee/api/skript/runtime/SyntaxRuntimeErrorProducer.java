package com.shanebeestudios.skbee.api.skript.runtime;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.elements.testing.elements.ExprLastRuntimeLogs;
import com.shanebeestudios.skbee.elements.testing.elements.SecTryCatch;

/**
 * Extension of Skript's SyntaxRuntimeErrorProducer which creates runtime errors
 * for use in {@link SecTryCatch} and {@link ExprLastRuntimeLogs}
 */
public interface SyntaxRuntimeErrorProducer extends org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer {


    @Override
    default void error(String message) {
        error(message, null);
    }

    @Override
    default void error(String message, String highlight) {
        if (TestMode.ENABLED) {
            Skript.error( message);
            return;
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.error(message);
    }

    @Override
    default void warning(String message) {
        warning(message, null);
    }

    @Override
    default void warning(String message, String highlight) {
        if (TestMode.ENABLED) {
            Skript.error( message);
            return;
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.warning(message);
    }

}
