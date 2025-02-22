package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.skript.base.Section;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Name("Repeating While Loop")
@Description({"Similar to Skript's while loop, this while loop will repeat at the given timespan.",
    "It is recommended to NOT use a wait within these sections, as the section will repeat regardless."})
@Examples({"on entity added to world:",
    "\tif event-entity is a wolf:",
    "\t\twhile event-entity is valid repeating every 1 seconds:",
    "\t\t\tset {_p} to nearest player in radius 15 around event-entity",
    "\t\t\tif {_p} is set:",
    "\t\t\t\tset target of event-entity to {_p}\n"})
@Since("INSERT VERSION")
public class SecWhileRunnable extends Section {

    static {
        Skript.registerSection(SecWhileRunnable.class, "while <.+> repeating every %timespan%");
    }

    private Condition condition;
    private Expression<Timespan> timespan;
    private Trigger section;
    private BukkitTask bukkitTask;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        String group = parseResult.regexes.getFirst().group();
        this.condition = Condition.parse(group, "some error");
        if (this.condition == null) return false;
        this.timespan = (Expression<Timespan>) exprs[0];
        if (this.timespan instanceof Literal<Timespan> literal) {
            if (literal.getSingle().getAs(Timespan.TimePeriod.TICK) < 1) {
                Skript.warning("You cannot repeat less than 1 tick, defaulting to 1 tick.");
            }
        }
        this.section = loadCode(sectionNode, "while loop", getParser().getCurrentEvents());
        if (this.section == null) return false;
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();
        Timespan timespan = this.timespan.getSingle(event);
        if (timespan == null) return next;

        long ticks = timespan.getAs(Timespan.TimePeriod.TICK);
        if (ticks < 1) ticks = 1;

        AtomicReference<Object> originalVars = new AtomicReference<>(Variables.copyLocalVariables(event));
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(SkBee.getPlugin(), () -> {
            Variables.setLocalVariables(event, originalVars.get());
            if (this.condition.check(event)) {
                TriggerItem.walk(this.section, event);
                originalVars.set(Variables.copyLocalVariables(event));
            } else {
                this.bukkitTask.cancel();
                TriggerItem.walk(next, event);
            }
        }, 0, ticks);
        return null;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "while " + this.condition.toString(e, d) + " repeating every " + this.timespan.toString(e, d);
    }

}
