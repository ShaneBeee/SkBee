package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;

public class EffTestLog extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffTestLog.class, "skbee [test] (log|:debug) %strings%")
            .noDoc()
            .register();
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
