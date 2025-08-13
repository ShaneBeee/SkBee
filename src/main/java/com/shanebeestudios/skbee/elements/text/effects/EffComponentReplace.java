package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("TextComponent - Replace Text")
@Description({"Replaces a given string with another string/text component.",
    "**NOTE:**",
    " - `regex` Defining the regex keyword will have the provided string be parsed as regex",
    " - `first` Defining the first keyword will only replace the first instance ",
    "If you're new to regex and want to see how it's parsed you can use https://regex101.com/ for debugging."})
@Examples({"component replace \"[item]\", \"[i]\" with getItemComponent(player's tool) in async chat message",
    "component regex replace \"\\[(item|i)]\" with getItemComponent(player's tool) in async chat message",
    "component replace first \"Mom!\" in {_message} with \"Dad!\""})
@Since("2.18.0")
public class EffComponentReplace extends Effect {

    static {
        Skript.registerEffect(EffComponentReplace.class,
            "component [:regex] replace [:first] %strings% with %object% in %~textcomponents%",
            "component [:regex] replace [:first] %strings% in %~textcomponents% with %object%");
    }

    private boolean useRegex, replaceFirst;
    private Expression<String> patterns;
    private Expression<?> replacement;
    private Expression<ComponentWrapper> components;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
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
        //noinspection UnstableApiUsage - Skript marks changeInPlace as internal but is fine to use
        this.components.changeInPlace(event, component -> {
            component.replace(this.useRegex, this.replaceFirst, replacement, patterns);
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
        return syntaxBuilder.toString();
    }

}
