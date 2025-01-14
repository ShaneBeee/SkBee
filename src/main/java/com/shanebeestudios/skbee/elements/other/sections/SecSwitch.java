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
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("SwitchCase - Switch")
@Description("")
@Examples("")
@Since("INSERT VERSION")
public class SecSwitch extends Section {

    public static class SwitchEvent extends Event {

        private final Object object;
        private final Event parentEvent;
        private final TriggerItem postSwitch;

        public SwitchEvent(Object object, Event parentEvent, TriggerItem postSwitch) {
            this.object = object;
            this.parentEvent = parentEvent;
            this.postSwitch = postSwitch;
        }

        public Object getObject() {
            return this.object;
        }

        public Event getParentEvent() {
            return this.parentEvent;
        }

        public TriggerItem getPostSwitch() {
            return this.postSwitch;
        }

        @Override
        @NotNull
        public HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }

    static {
        Skript.registerSection(SecSwitch.class, "switch %object%");
    }

    private Expression<Object> object;
    private Trigger caseSection;
    public TriggerItem dirty;

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.object = LiteralUtils.defendExpression(exprs[0]);
        Class<? extends Event>[] currentEvents = getParser().getCurrentEvents();
        Class<? extends Event>[] events = new Class[currentEvents.length + 1];
        System.arraycopy(currentEvents, 0, events, 0, currentEvents.length);
        events[currentEvents.length] = SwitchEvent.class;

        this.caseSection = loadCode(sectionNode, "switch case", events);

        if (this.dirty != null) {
            Skript.error("Only cases can be used in a switch section but found this: \n    ==> '" + this.dirty + "'");
            return false;
        }

        return LiteralUtils.canInitSafely(this.object);
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object object = this.object.getSingle(event);
        if (object != null) {
            Trigger.walk(this.caseSection, new SwitchEvent(object, event, getNext()));
        }
        if (getNext() == null) {
            return null;
        }
        return super.walk(event, false);
    }

    public Expression<Object> getObjectExpression() {
        return this.object;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "switch " + this.object.toString(e, d);
    }

}
