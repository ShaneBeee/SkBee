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
import ch.njol.skript.lang.LoopSection;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
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
@Since("3.9.0")
public class SecWhileRunnable extends LoopSection {

    static {
        Skript.registerSection(SecWhileRunnable.class, "while <.+> repeating every %timespan%");
    }

    private Condition condition;
    private Expression<Timespan> timespan;
    private BukkitTask bukkitTask;
    private TriggerItem next;

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
        loadCode(sectionNode);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        this.next = getNext();
        Timespan timespan = this.timespan.getSingle(event);
        if (timespan == null) return this.next;

        long ticks = timespan.getAs(Timespan.TimePeriod.TICK);
        if (ticks < 1) ticks = 1;

        AtomicReference<Object> originalVars = new AtomicReference<>(Variables.copyLocalVariables(event));
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(SkBee.getPlugin(), () -> {
            Variables.setLocalVariables(event, originalVars.get());
            if (this.condition.check(event)) {
                TriggerItem.walk(this.first, event);
                originalVars.set(Variables.copyLocalVariables(event));
            } else {
                exit(event);
                TriggerItem.walk(this.next, event);
            }
        }, 0, ticks);
        if (this.last != null) this.last.setNext(null);
        return null;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "while " + this.condition.toString(e, d) + " repeating every " + this.timespan.toString(e, d);
    }

    @Override
    public TriggerItem getActualNext() {
        return this.next;
    }

    @Override
    public void exit(Event event) {
        this.bukkitTask.cancel();
    }

}
