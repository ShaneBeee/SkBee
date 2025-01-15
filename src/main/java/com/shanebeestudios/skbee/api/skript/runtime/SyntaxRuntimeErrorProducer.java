package com.shanebeestudios.skbee.api.skript.runtime;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.elements.testing.elements.ExprLastRuntimeLogs;
import com.shanebeestudios.skbee.elements.testing.elements.SecTryCatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extension of Skript's SyntaxRuntimeErrorProducer which creates runtime errors
 * for use in {@link SecTryCatch} and {@link ExprLastRuntimeLogs}
 */
public interface SyntaxRuntimeErrorProducer extends org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer {


    @Override
    default void error(String message) {
        error(message, null);
    }

    default void errorRegex(String message, String regex) {
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        String fullLine = getNode().save().trim();
        Pattern pattern = Pattern.compile("(" + regex + ")");
        Matcher matcher = pattern.matcher(fullLine);
        if (matcher.find()) {
            regex = matcher.group(1);
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.error(message, regex);
    }

    @Override
    default void error(String message, String highlight) {
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.error(message, highlight);
    }

    @Override
    default void warning(String message) {
        warning(message, null);
    }

    default void warningRegex(String message, String regex) {
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        String fullLine = getNode().save().trim();
        Pattern pattern = Pattern.compile("(" + regex + ")");
        Matcher matcher = pattern.matcher(fullLine);
        if (matcher.find()) {
            regex = matcher.group(1);
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.warning(message, regex);
    }

    @Override
    default void warning(String message, String highlight) {
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.warning(message, highlight);
    }

}
