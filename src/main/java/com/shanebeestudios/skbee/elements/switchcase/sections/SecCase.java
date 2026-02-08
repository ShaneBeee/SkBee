package com.shanebeestudios.skbee.elements.switchcase.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.CondCompare;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionSection;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.ReturnHandler;
import ch.njol.skript.lang.ReturnableTrigger;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchBaseEvent;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchReturnEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.List;

@Name("SwitchCase - Case")
@Description({"In a switch section/expression, you can manage different actions based on the case matching the switched value.",
    "Multiple objects are supported in cases.",
    "Default will run if all other cases fail to match. Default must go last or all cases after it will be ignored."})
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
public class SecCase extends Section implements ReturnHandler<Object> {

    static {
        Skript.registerSection(SecCase.class, "case %objects%", "(default [case]|case default)");
    }

    private Expression<Object> caseObject;
    private boolean defaultCase;
    private ReturnableTrigger<?> caseSection;
    private Object[] returnObject;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        Section switchSection = null;
        Expression<?> switchObject = null;
        if (getParser().getCurrentStructure() instanceof SectionSkriptEvent skriptEvent) {
            if (skriptEvent.getSection() instanceof SecSwitch secSwitch) {
                switchSection = secSwitch;
                switchObject = secSwitch.getSwitchedObjectExpression();

            } else if (skriptEvent.getSection() instanceof ExpressionSection expressionSection) {
                if (expressionSection.getAsExpression() instanceof SecExprSwitchReturn secExprSwitchReturn) {
                    switchSection = expressionSection;
                    switchObject = secExprSwitchReturn.getSwitchedObjectExpression();
                }
            }
        }
        if (switchSection == null) {
            Skript.error("Cases can only be used in a switch section/expression.");
            return false;
        }
        if (matchedPattern == 1) {
            this.defaultCase = true;
        } else {
            this.caseObject = LiteralUtils.defendExpression(exprs[0]);
            if (switchObject != null && this.caseObject instanceof Literal<?> literal) {
                for (Object lit : literal.getArray()) {
                    Class<?> switchReturnType = switchObject.getReturnType();
                    if (!canCompare(switchReturnType, lit.getClass())) {
                        Skript.error("Can't compare " + CondCompare.f(switchObject) + " with " + CondCompare.f(literal));
                        return false;
                    }
                }
            }
        }
        if (sectionNode != null) {
            this.caseSection = loadReturnableSectionCode(sectionNode, "case section", getParser().getCurrentEvents());
        }
        return this.defaultCase || LiteralUtils.canInitSafely(this.caseObject);
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (event instanceof SwitchBaseEvent switchEvent) {
            if (this.defaultCase || compare(this.caseObject.getArray(event), switchEvent.getSwitchedObject())) {
                Trigger.walk(this.caseSection, switchEvent.getParentEvent());
                if (event instanceof SwitchReturnEvent switchReturnEvent) {
                    if (this.returnObject != null) switchReturnEvent.setReturnedObject(this.returnObject);
                }
                // TODO somehow handle functions?!?!
                return null;
            } else if (getActualNext() != null) {
                return super.walk(event, false);
            }
        }
        return null;
    }

    @Override
    public String toString(Event e, boolean d) {
        if (this.defaultCase) return "default case";
        return "case " + this.caseObject.toString(e, d);
    }

    public static boolean canCompare(Class<?> c1, Class<?> c2) {
        if (c1 == Object.class || c2 == Object.class) return true;
        return Comparators.comparatorExists(c1, c2);
    }

    public static boolean compare(Object[] comparables, Object source) {
        if (comparables == null || source == null) return false;
        for (Object comparable : comparables) {
            if (Comparators.comparatorExists(comparable.getClass(), source.getClass())) {
                if (Comparators.compare(source, comparable) == Relation.EQUAL) {
                    return true;
                }
            }
        }
        return comparables.equals(source);
    }


    @Override
    public void returnValues(Event event, Expression<?> value) {
        this.returnObject = value.getArray(event);
    }

    @Override
    public boolean isSingleReturnValue() {
        return true;
    }

    @Override
    public @Nullable Class<?> returnValueType() {
        return Object.class;
    }

}
