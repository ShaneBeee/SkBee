package com.shanebeestudios.skbee.api.skript.runtime;

import ch.njol.skript.Skript;
import ch.njol.skript.test.runner.TestMode;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.testing.elements.ExprLastRuntimeLogs;
import com.shanebeestudios.skbee.elements.testing.elements.SecTryCatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extension of Skript's SyntaxRuntimeErrorProducer which creates runtime errors
 * for use in {@link SecTryCatch} and {@link ExprLastRuntimeLogs}
 */
public interface SyntaxRuntimeErrorProducer extends org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer {

    boolean DISABLE_ERROR = SkBee.getPlugin().getPluginConfig().RUNTIME_DISABLE_ERRORS;
    boolean DISABLE_WARNING = SkBee.getPlugin().getPluginConfig().RUNTIME_DISABLE_WARNINGS;

    @Override
    default void error(String message) {
        error(message, null);
    }

    default void errorRegex(String message, String regex) {
        if (DISABLE_ERROR) return;
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        if (regex != null && getNode() != null) {
            String fullLine = getNode().save().trim();
            Pattern pattern = Pattern.compile("(" + regex + ")");
            Matcher matcher = pattern.matcher(fullLine);
            if (matcher.find()) {
                regex = matcher.group(1);
            }
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.error(message, regex);
    }

    @Override
    default void error(String message, String highlight) {
        if (DISABLE_ERROR) return;
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
        if (DISABLE_WARNING) return;
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        if (regex != null && getNode() != null) {
            String fullLine = getNode().save().trim();
            Pattern pattern = Pattern.compile("(" + regex + ")");
            Matcher matcher = pattern.matcher(fullLine);
            if (matcher.find()) {
                regex = matcher.group(1);
            }
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.warning(message, regex);
    }

    @Override
    default void warning(String message, String highlight) {
        if (DISABLE_WARNING) return;
        if (TestMode.ENABLED) {
            Skript.error(message);
            return;
        }
        org.skriptlang.skript.log.runtime.SyntaxRuntimeErrorProducer.super.warning(message, highlight);
    }

}
