package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.SkriptConfig;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffComponentReplace extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffComponentReplace.class,
                "component [:regex] replace [:first] %strings% with %object% in %~textcomponents% [case:with case sensitivity]",
                "component [:regex] replace [:first] %strings% in %~textcomponents% with %object% [case:with case sensitivity]")
            .name("TextComponent - Replace Text")
            .description(
                "Replaces a given string with another string/text component.",
                "**NOTE:**",
                " - `regex` Defining the regex keyword will have the provided string be parsed as regex.",
                " - `first` Defining the first keyword will only replace the first instance. ",
                " - Any case-sensitivity checks only apply to literal patterns, for regex append `(?i)` to the start",
                "If you're new to regex and want to see how it's parsed you can use https://regex101.com/ for debugging.")
            .examples(
                "component replace \"[item]\", \"[i]\" with getItemComponent(player's tool) in async chat message",
                "component regex replace \"\\[(item|i)]\" with getItemComponent(player's tool) in async chat message",
                "component replace first \"Mom!\" in {_message} with \"Dad!\" with case sensitivity")
            .since("2.18.0")
            .register();
    }

    private boolean useRegex, replaceFirst, caseSensitive;
    private Expression<String> patterns;
    private Expression<?> replacement;
    private Expression<ComponentWrapper> components;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.caseSensitive = parseResult.hasTag("case");
        this.useRegex = parseResult.hasTag("regex");
        this.replaceFirst = parseResult.hasTag("first");
        this.patterns = (Expression<String>) exprs[0];
        this.components = (Expression<ComponentWrapper>) exprs[(2 - matchedPattern % 2)];
        this.replacement = LiteralUtils.defendExpression(exprs[(1 + matchedPattern % 2)]);
        return LiteralUtils.canInitSafely(this.replacement);
    }

    @Override
    protected void execute(Event event) {
        Object replacement = this.replacement.getSingle(event);
        String[] patterns = this.patterns.getArray(event);
        if (replacement == null || patterns.length == 0) return;

        boolean caseSensitive = this.caseSensitive;
        if (!caseSensitive) caseSensitive = SkriptConfig.caseSensitive.value();

        final boolean finalCaseSensitive = caseSensitive;
        //noinspection UnstableApiUsage - Skript marks changeInPlace as internal but is safe to use
        this.components.changeInPlace(event, component -> {
            component.replace(this.useRegex, this.replaceFirst, finalCaseSensitive, replacement, patterns);
            return component;
        });
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        SyntaxStringBuilder syntaxBuilder = new SyntaxStringBuilder(event, debug);
        syntaxBuilder.append("component");
        if (this.useRegex) syntaxBuilder.append("regex");
        syntaxBuilder.append("replace");
        if (this.replaceFirst) syntaxBuilder.append("first");
        syntaxBuilder.append(this.patterns, "with", this.replacement, "in", this.components);
        if (this.caseSensitive) syntaxBuilder.append("with case sensitivity");
        return syntaxBuilder.toString();
    }

}
