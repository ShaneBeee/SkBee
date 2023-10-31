package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Run Task Later")
@Description({"Run a task later. Similar to Skript's delay effect, with the difference being everything in the",
        "section is run later. All code after your section will keep running as normal without a delay.",
        "This can be very useful in loops, to prevent halting the loop."})
@Examples({"on explode:",
        "\tloop exploded blocks:",
        "\t\tset {_loc} to location of loop-block",
        "\t\tset {_data} to block data of loop-block",
        "\t\trun 2 seconds later:",
        "\t\t\tset block at {_loc} to {_data}\n"})
@Since("INSERT VERSION")
public class SecRunTaskLater extends Section {

    private static final Plugin PLUGIN = SkBee.getPlugin();

    static {
        Skript.registerSection(SecRunTaskLater.class, "(run|execute) %timespan% later");
    }

    private Expression<Timespan> timespan;
    private Trigger trigger;

    @SuppressWarnings({"NullableProblems", "unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.timespan = (Expression<Timespan>) exprs[0];
        this.trigger = loadCode(sectionNode, "run later", ParserInstance.get().getCurrentEvents());
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        Timespan timespan = this.timespan.getSingle(event);
        if (timespan == null) timespan = Timespan.fromTicks_i(0);

        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
            Variables.setLocalVariables(event, localVars);
            TriggerItem.walk(trigger, event);
            Variables.setLocalVariables(event, Variables.copyLocalVariables(event));
            Variables.removeLocals(event);
        }, timespan.getTicks_i());

        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "run " + this.timespan.toString(e, d) + " later";
    }

}
