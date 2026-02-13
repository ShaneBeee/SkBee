package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSleepThread extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSleepThread.class, "(sleep|halt) thread for %timespan%")
            .name("Sleep Thread")
            .description("This effect is used to sleep a thread.",
                "This should **NOT** be used on the main server thread.")
            .examples("on async player connection configure:",
                "\tset {-connect::%event-uuid%} to true",
                "",
                "\t# Do something",
                "",
                "\twhile {-connect::%event-uuid%} is set:",
                "\t\t# Player login will be halted while we wait for something",
                "\t\tsleep thread for 1 tick",
                "\t#Player will now connect")
            .since("3.15.0")
            .register();
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
