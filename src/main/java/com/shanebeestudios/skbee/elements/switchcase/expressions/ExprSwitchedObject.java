package com.shanebeestudios.skbee.elements.switchcase.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionSection;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SectionSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.switchcase.events.SwitchBaseEvent;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecExprSwitchReturn;
import com.shanebeestudios.skbee.elements.switchcase.sections.SecSwitch;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("SwitchCase - Switched Value")
@Description("Represents the object that was 'switched' in a switch section/expression.")
@Examples({"function getBiome(b: biome) :: string:",
    "\treturn switch return {_b}:",
    "\t\tcase plains, sunflower plains, beach -> \"&a%switched value%\"",
    "\t\tcase desert, savanna, badlands -> \"&e%switched value%\"",
    "\t\tcase snowy beach, frozen peaks, grove -> \"&b%switched value%\"",
    "\t\tdefault -> \"&7%switched value%\""})
@Since("INSERT VERSION")
public class ExprSwitchedObject extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprSwitchedObject.class, Object.class, ExpressionType.EVENT,
            "[the] switched (object|value)");
    }

    private Section switchSection;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (getParser().getCurrentStructure() instanceof SectionSkriptEvent skriptEvent) {
            if (skriptEvent.getSection() instanceof SecSwitch secSwitch) {
                this.switchSection = secSwitch;

            } else if (skriptEvent.getSection() instanceof ExpressionSection expressionSection) {
                if (expressionSection.getAsExpression() instanceof SecExprSwitchReturn) {
                    this.switchSection = expressionSection;
                }
            }
        }
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        if (event instanceof SwitchBaseEvent switchBaseEvent) {
            return new Object[]{switchBaseEvent.getSwitchedObject()};
        } else if (this.switchSection instanceof SecSwitch secSwitch) {
            Expression<?> object = secSwitch.getSwitchedObjectExpression();
            if (object != null) {
                return new Object[]{object.getSingle(event)};
            }
        } else if (this.switchSection instanceof ExpressionSection expressionSection) {
            if (expressionSection.getAsExpression() instanceof SecExprSwitchReturn secExprSwitchReturn) {

                Expression<?> object = secExprSwitchReturn.getSwitchedObjectExpression();
                if (object != null) {
                    return new Object[]{object.getSingle(event)};
                }
            }
        }
        return null;
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
    public String toString(@Nullable Event event, boolean debug) {
        return "the switched value";
    }

}
