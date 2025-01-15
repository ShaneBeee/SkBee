package com.shanebeestudios.skbee.elements.switchcase.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchReturnEvent;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchSecEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("SwitchCase - Switch Return")
@Description("Switch an object and have it return a value.")
@Examples({"function getRoman(i: number) :: string:",
    "\treturn switch return {_i}:",
    "\t\tcase 1 -> \"I\"",
    "\t\tcase 2 -> \"II\"",
    "\t\tcase 3 -> \"III\"",
    "\t\tcase 4 -> \"IV\"",
    "\t\tcase 5 -> \"V\"",
    "\t\tdefault -> \"potato\"",
    "",
    "function getName(e: entity) :: string:",
    "\treturn switch return {_e}:",
    "\t\tcase sheep -> \"Mr Sheepy\"",
    "\t\tcase cow -> \"Mr Cow\"",
    "\t\tcase pig -> \"Señor Pig\"",
    "\t\tdefault -> strict proper case \"%type of {_e}%\"",
    "",
    "on break:",
    "\tset {_i} to switch return event-block:",
    "\t\tcase stone -> \"Stoney Stone\"",
    "\t\tcase dirt -> \"Dirty Dirt\"",
    "\t\tcase grass block -> \"Grassy Grass\"",
    "\t\tdefault:",
    "\t\t\tif gamemode of player = creative:",
    "\t\t\t\treturn \"&c%type of switched object%\"",
    "\t\t\telse:",
    "\t\t\t\treturn \"&a%type of switched object%\"",
    "",
    "\tsend \"Broken: %{_i}%\" to player"})
@Since("3.8.0")
public class SecExprSwitchReturn extends SectionExpression<Object> {

    static {
        Skript.registerExpression(SecExprSwitchReturn.class, Object.class, ExpressionType.COMBINED,
            "(switch return|return switch) %object%"); // TODO better pattern? (can't use `switch object` again)
    }

    private Expression<?> switchedObject;
    private Trigger section;

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean delayed, ParseResult result, @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        this.switchedObject = LiteralUtils.defendExpression(exprs[0]);

        Class<? extends Event>[] currentEvents = getParser().getCurrentEvents();
        Class<? extends Event>[] events = new Class[currentEvents.length + 1];
        System.arraycopy(currentEvents, 0, events, 0, currentEvents.length);
        events[currentEvents.length] = SwitchSecEvent.class;
        this.section = loadCode(node, "switch section expression", null, events);
        return LiteralUtils.canInitSafely(this.switchedObject);
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        Object object = this.switchedObject.getSingle(event);
        if (object == null) return null;

        Object variables = Variables.copyLocalVariables(event);
        SwitchReturnEvent returnEvent = new SwitchReturnEvent(object, event);
        Variables.setLocalVariables(returnEvent, variables);
        Trigger.walk(this.section, returnEvent);
        Variables.setLocalVariables(event, Variables.copyLocalVariables(returnEvent));

        return new Object[]{returnEvent.getReturnedObject()};
    }

    public Expression<?> getSwitchedObjectExpression() {
        return this.switchedObject;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "switch return " + this.switchedObject.toString(e, d);
    }

}
