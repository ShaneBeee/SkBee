package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.CondCompare;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.sections.SecSwitch.SwitchEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.List;

@Name("SwitchCase - Case")
@Description("")
@Examples("")
@Since("INSERT VERSION")
public class SecCase extends Section {

    static {
        Skript.registerSection(SecCase.class, "case %object%", "(default [case]|case default)");
    }

    private Expression<Object> object;
    private boolean defaultCase;
    private Trigger caseSection;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!(getParser().getCurrentStructure() instanceof SectionSkriptEvent skriptEvent) || !(skriptEvent.getSection() instanceof SecSwitch secSwitch)) {
            Skript.error("cases can only be used in a switch section.");
            return false;
        }
        for (TriggerItem triggerItem : triggerItems) {
            if (!(triggerItem instanceof SecCase)) {
                secSwitch.dirty = triggerItem;
            }
        }
        if (matchedPattern == 1) {
            this.defaultCase = true;
        } else {
            this.object = LiteralUtils.defendExpression(exprs[0]);
            if (secSwitch != null && this.object instanceof Literal<Object> literal) {
                Class<?> literalReturnType = literal.getReturnType();
                Class<?> switchReturnType = secSwitch.getObjectExpression().getReturnType();
                if (!canCompare(switchReturnType, literalReturnType)) {
                    Skript.error("Can't compare " + CondCompare.f(secSwitch.getObjectExpression()) + " with " + CondCompare.f(literal));
                    return false;
                }
            }
        }
        if (sectionNode != null) {
            this.caseSection = loadCode(sectionNode, "case section", getParser().getCurrentEvents());
        }
        return this.defaultCase || LiteralUtils.canInitSafely(this.object);
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (event instanceof SwitchEvent switchEvent) {
            if (this.defaultCase || compare(this.object.getSingle(event), switchEvent.getObject())) {
                Trigger.walk(this.caseSection, switchEvent.getParentEvent());
                return null;
            } else if (getActualNext() instanceof SecCase) {
                return super.walk(event, false);
            }
        }
        return null;
    }

    @Override
    public String toString(Event e, boolean d) {
        if (this.defaultCase) return "default case";
        return "case " + this.object.toString(e, d);
    }

    private boolean canCompare(Class<?> c1, Class<?> c2) {
        if (c1 == Object.class || c2 == Object.class) return true;
        return Comparators.comparatorExists(c1, c2);
    }

    private boolean compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) return false;
        if (Comparators.comparatorExists(o1.getClass(), o2.getClass())) {
            return Comparators.compare(o1, o2) == Relation.EQUAL;
        }
        return o1.equals(o2);
    }


}
