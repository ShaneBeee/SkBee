package com.shanebeestudios.skbee.elements.switchcase.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.CondCompare;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionSection;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchReturnEvent;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecCase;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecExprSwitchReturn;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("SwitchCase - Case Return")
@Description({"In a switch return expression, you can return different values based on the case matching the switched value.",
    "Multiple objects are supported in cases.",
    "Default will run if all other cases fail to match. Default must go last or all cases after it will be ignored."})
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
    "\tset {_b} to event-block",
    "\tset {_i} to switch return {_b}:",
    "\t\tcase stone -> 1 of stone named \"Hard Stone\"",
    "\t\tcase grass block -> 1 of grass block named \"Green Grass\"",
    "\t\tcase dirt -> 1 of dirt named \"Dry Dirt\"",
    "\t\tdefault -> 1 of {_b} named \"Some Other Block\"",
    "\tgive player {_i}",
    "",
    "on damage of a mob by a player:",
    "\tset {_item} to switch return type of victim:",
    "\t\tcase sheep, cow, pig, chicken -> 1 of potato",
    "\t\tcase zombie, drowned, husk -> 1 of rotten flesh",
    "\t\tcase skeleton, stray, wither skeleton, bogged -> 1 of bone",
    "\t\tdefault -> 1 of stick",
    "\tgive {_item} to attacker\n"})
@Since("INSERT VERSION")
public class EffCaseReturn extends Effect {

    static {
        Skript.registerEffect(EffCaseReturn.class,
            "case %objects% -> %object%",
            "default -> %object%");
    }

    private boolean defaultCase;
    private Expression<?> caseObject;
    private Expression<?> returnObject;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        Section switchSection = null;
        Expression<?> switchObject = null;
        if (getParser().getCurrentStructure() instanceof SectionSkriptEvent skriptEvent) {
//            if (skriptEvent.getSection() instanceof SecSwitch secSwitch) {
//                switchSection = secSwitch;
//                switchObject = secSwitch.getObjectExpression();
//
//            } else TODO maybe support this in the future? (`case %object% -> %effect%`)?!?!
            if (skriptEvent.getSection() instanceof ExpressionSection expressionSection) {
                if (expressionSection.getAsExpression() instanceof SecExprSwitchReturn secExprSwitchReturn) {
                    switchSection = expressionSection;
                    switchObject = secExprSwitchReturn.getObjectExpression();
                }
            }
        }
        if (switchSection == null) {
            Skript.error("Cases returns can only be used in a switch expression section.");
            return false;
        }

        this.returnObject = LiteralUtils.defendExpression(exprs[matchedPattern ^ 1]);
        if (matchedPattern == 1) {
            this.defaultCase = true;
        } else {
            this.caseObject = LiteralUtils.defendExpression(exprs[0]);
            if (switchObject != null && this.caseObject instanceof Literal<?> literal) {
                for (Object lit : literal.getArray()) {
                    Class<?> switchReturnType = switchObject.getReturnType();
                    if (!SecCase.canCompare(switchReturnType, lit.getClass())) {
                        Skript.error("Can't compare " + CondCompare.f(switchObject) + " with " + CondCompare.f(literal));
                        return false;
                    }
                }
            }
        }
        return LiteralUtils.canInitSafely(this.returnObject) && (this.defaultCase || LiteralUtils.canInitSafely(this.caseObject));
    }

    @Override
    protected void execute(Event event) {
        // We have to walk instead
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (event instanceof SwitchReturnEvent switchReturnEvent) {
            if (this.defaultCase || SecCase.compare(this.caseObject.getArray(event), switchReturnEvent.getSwitchedObject())) {
                Object returnObject = this.returnObject.getSingle(event);
                if (returnObject != null) {
                    switchReturnEvent.setReturnedObject(returnObject);
                    return null;
                }
            }
        }
        return super.walk(event);
    }

    @Override
    public String toString(Event e, boolean d) {
        if (this.defaultCase) return "default -> " + this.returnObject.toString(e, d);
        return "case " + this.caseObject.toString(e, d) + " -> " + this.returnObject.toString(e, d);
    }

}
