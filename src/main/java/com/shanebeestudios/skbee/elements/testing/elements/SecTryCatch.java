package com.shanebeestudios.skbee.elements.testing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.log.LogEntry;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.test.runner.TestTracker;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.Section;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NoDoc
public class SecTryCatch extends Section {

    static {
        Skript.registerSection(SecTryCatch.class,
            "try", // Used to test for runtime errors
            "catch" // Used to catch the runtime errrors
            // If no catch block is used, runtime errors are sent to TestTracker
        );
    }

    private Trigger trigger;
    private boolean isCaught;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.isCaught = matchedPattern == 1;
        String secName = this.isCaught ? "catch section" : "try section";
        this.trigger = loadCode(sectionNode, secName, getParser().getCurrentEvents());
        return true;
    }

    @SuppressWarnings("resource")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        RetainingLogHandler handler = SkriptLogger.startRetainingLog();
        Trigger.walk(this.trigger, event);

        ExprLastRuntimeLogs.errors = handler.getLog().stream().map(LogEntry::getMessage).toArray(String[]::new);
        handler.stop();
        if (!(getNext() instanceof SecTryCatch secTryCatch) || !secTryCatch.isCaught) {
            for (String error : ExprLastRuntimeLogs.errors) {
                TestTracker.testFailed("runtime error: " + error);
            }
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return this.isCaught ? "catch" : "try";
    }

}
