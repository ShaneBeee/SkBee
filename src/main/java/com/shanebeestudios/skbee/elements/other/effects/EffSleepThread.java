package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Sleep Thread")
@Description({"This effect is used to sleep a thread.",
    "This should **NOT** be used on the main server thread."})
@Examples({"on async player connection configure:",
    "\tset {-connect::%event-uuid%} to true",
    "",
    "\t# Do something",
    "",
    "\twhile {-connect::%event-uuid%} is set:",
    "\t\t# Player login will be halted while we wait for something",
    "\t\tsleep thread for 1 tick",
    "\t#Player will now connect"})
@Since("INSERT VERSION")
public class EffSleepThread extends Effect {

    static {
        Skript.registerEffect(EffSleepThread.class, "(sleep|halt) thread for %timespan%");
    }

    private Expression<Timespan> timespan;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.timespan = (Expression<Timespan>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Timespan timespan = this.timespan.getSingle(event);
        if (timespan == null) return;

        long millis = timespan.getAs(Timespan.TimePeriod.MILLISECOND);
        if (millis <= 0) return;

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            error("InterruptedException: " + ex.getMessage());
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "sleep thread for " + this.timespan.toString(e, d);
    }

}
