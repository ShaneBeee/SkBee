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
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchReturnEvent;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchSecEvent;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecCase;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecExprSwitchReturn;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecSwitch;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("SwitchCase - Case Inline")
@Description({"Inline version of case section where you can return/run an effect all in one line.",
    "Multiple objects are supported in cases.",
    "- **Switch Expression** = Return an object based on the case matching the switched value.",
    "- **Switch Section** = Run 1 effect based on the case matching the switched value.",
    "Default will run if all other cases fail to match. Default must go last or all cases after it will be ignored."})
@Examples({"# As Effect",
    "on damage of a sheep by a player:",
    "\tswitch type of attacker's tool:",
    "\t\tcase wooden sword -> give attacker yellow wool",
    "\t\tcase stone sword -> give attacker light gray wool",
    "\t\tcase iron sword -> give attacker gray wool",
    "\t\tcase golden sword -> give attacker orange wool",
    "\t\tcase diamond sword -> give attacker light blue wool",
    "\t\tcase netherite sword -> give attacker black wool",
    "\t\tdefault -> give attacker white wool",
    "",
    "# As Return",
    "function getRoman(i: number) :: string:",
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
    "\tgive {_item} to attacker",})
@Since("3.8.0")
public class EffCase extends Effect {

    static {
        Skript.registerEffect(EffCase.class,
            "case %objects% -> <.+>",
            "default -> <.+>");
    }

    private boolean defaultCase;
    private Expression<?> caseObject;
    private Expression<?> returnObject;
    private Effect effect;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
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
            Skript.error("Cases returns can only be used in a switch expression section.");
            return false;
        }

        String group = parseResult.regexes.getFirst().group();
        if (switchSection instanceof SecSwitch) {
            Effect effect = Effect.parse(group, "Cannot understand this effect: '" + group + "'");
            if (effect != null) {
                this.effect = effect;
            } else {
                return false;
            }
        } else {
            Expression<?> expression = SkriptUtils.parseExpression(group);
            if (expression != null) {
                this.returnObject = expression;
            } else {
                return false;
            }
        }
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
        if (this.defaultCase || LiteralUtils.canInitSafely(this.caseObject)) {
            if (this.returnObject != null) {
                return LiteralUtils.canInitSafely(this.returnObject);
            } else if (this.effect != null) {
                return true;
            }
        }
        return false;
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
        } else if (event instanceof SwitchSecEvent switchSecEvent) {
            if (this.defaultCase || SecCase.compare(this.caseObject.getArray(event), switchSecEvent.getSwitchedObject())) {
                if (this.effect != null) {
                    TriggerItem.walk(this.effect, switchSecEvent.getParentEvent());
                    return null;
                }
            }
        }
        return super.walk(event);
    }

    @Override
    public String toString(Event e, boolean d) {
        String caseType = "broken";
        if (this.effect != null) {
            caseType = this.effect.toString(e, d);
        } else if (this.returnObject != null) {
            caseType = this.returnObject.toString(e, d);
        }
        if (this.defaultCase) return "default -> " + caseType;
        return "case " + this.caseObject.toString(e, d) + " -> " + caseType;
    }

}
