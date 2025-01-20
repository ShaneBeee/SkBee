package com.shanebeestudios.skbee.elements.switchcase.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
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
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchSecEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("SwitchCase - Switch")
@Description("Switch an object and use cases to manage different actions.")
@Examples({"on break:",
    "\tswitch event-block:",
    "\t\tcase dirt:",
    "\t\t\tgive player a stick named \"Dirt\"",
    "\t\tcase stone:",
    "\t\t\tgive player an apple named \"Stone\"",
    "\t\tcase grass block:",
    "\t\t\tgive player an iron ingot named \"Iron Ingot\"",
    "\t\tdefault:",
    "\t\t\tkill player",
    "",
    "on damage of a mob by a player:",
    "\tswitch type of victim:",
    "\t\tcase zombie, husk, a drowned:",
    "\t\t\tspawn 3 baby zombies at victim",
    "\t\tcase skeleton, stray:",
    "\t\t\tspawn a skeleton horse at victim:",
    "\t\t\t\tset {_h} to entity",
    "\t\t\tspawn a skeleton at victim:",
    "\t\t\t\tset {_s} to entity",
    "\t\t\tmake {_s} ride {_h}",
    "\t\tcase sheep, cow, chicken, pig:",
    "\t\t\tkill attacker",
    "\t\tdefault:",
    "\t\t\tgive attacker a diamond"})
@Since("3.8.0")
public class SecSwitch extends Section {

    static {
        Skript.registerSection(SecSwitch.class, "switch %object%");
    }

    private Expression<?> switchedObject;
    private Trigger caseSection;

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.switchedObject = LiteralUtils.defendExpression(exprs[0]);
        Class<? extends Event>[] currentEvents = getParser().getCurrentEvents();
        Class<? extends Event>[] events = new Class[currentEvents.length + 1];
        System.arraycopy(currentEvents, 0, events, 0, currentEvents.length);
        events[currentEvents.length] = SwitchSecEvent.class;

        this.caseSection = loadCode(sectionNode, "switch case", events);

        // Search through the section and see if the lines are cases
        for (Node node : sectionNode) {
            String key = node.getKey();
            if (!key.startsWith("case") && !key.startsWith("default")) {
                Skript.error("Only cases can be used in a switch section but found this:");
                this.caseSection = null;
                break;
            }
        }

        return LiteralUtils.canInitSafely(this.switchedObject);
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object object = this.switchedObject.getSingle(event);
        if (object != null) {
            SwitchSecEvent switchSecEvent = new SwitchSecEvent(object, event, getNext());
            Trigger.walk(this.caseSection, switchSecEvent);
        }
        if (getNext() == null) {
            return null;
        }
        return super.walk(event, false);
    }

    public Expression<?> getSwitchedObjectExpression() {
        return this.switchedObject;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "switch " + this.switchedObject.toString(e, d);
    }

}
