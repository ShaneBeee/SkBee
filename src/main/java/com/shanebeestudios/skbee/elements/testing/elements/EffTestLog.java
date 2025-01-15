package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;

@NoDoc
public class EffTestLog extends Effect {

    static {
        Skript.registerEffect(EffTestLog.class, "skbee [test] (log|:debug) %strings%");
    }

    private Expression<String> logs;
    private boolean debug;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.logs = (Expression<String>) exprs[0];
        this.debug = parseResult.hasTag("debug");
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (String log : this.logs.getArray(event)) {
            if (this.debug) {
                Util.debug(log);
            } else {
                Util.log(log);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String name = this.debug ? "debug " : "info ";
        return "skbee " + name + this.logs.toString(e, d);
    }

}
