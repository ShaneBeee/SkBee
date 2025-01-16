package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.VerboseAssert;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class CondClassInfoRegistered extends Condition implements VerboseAssert {

    static {
        Skript.registerCondition(CondClassInfoRegistered.class,
            "class info (by class|id:with codename) %string% (is|neg:isn't) registered");
    }

    private Expression<String> name;
    private boolean codename;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(parseResult.hasTag("neg"));
        this.name = (Expression<String>) exprs[0];
        this.codename = parseResult.hasTag("id");
        return true;
    }

    @Override
    public boolean check(Event event) {
        return this.name.check(event, name -> {
            if (codename) {
                return Classes.getClassInfoNoError(name) != null;
            } else {
                try {
                    Class<?> aClass = Class.forName(name);
                    return Classes.getExactClassInfo(aClass) != null;
                } catch (ClassNotFoundException ignored) {
                }
            }
            return false;
        }, isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return new SyntaxStringBuilder(event, debug)
            .append("class info")
            .append(this.codename ? "with codename" : "by class", this.name)
            .append(isNegated() ? "isn't" : "is", "registered")
            .toString();
    }

    @Override
    public String getExpectedMessage(Event event) {
        return "expected class info '" + this.name.getSingle(event) + "'";
    }

    @Override
    public String getReceivedMessage(Event event) {
        return "not registered";
    }

}
