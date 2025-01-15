package com.shanebeestudios.skbee.elements.objective.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;

import java.util.function.Predicate;

@Name("Scoreboard - Objective/Critera Modifiable")
@Description({"Check if an objective/criteria has modifiable scores.",
    "Some criteria such as 'health' cannot be modified.",
    "See [**Criteria**](https://minecraft.wiki/w/Scoreboard#Criteria) on McWiki for more info."})
@Examples({"if the scores of {_objective} are modifiable:",
    "if the scores of criteria with id \"some_criteria\" are modifiable:"})
@Since("INSERT VERSION")
public class CondObjModifiable extends Condition {

    static {
        Skript.registerCondition(CondObjModifiable.class,
            "[the] scores of %criterias/objectives% are modifiable",
            "[the] scores of %criterias/objectives% (aren't|are not) modifiable");
    }

    private Expression<?> objects;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objects = exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return this.objects.check(event, new Predicate<Object>() {
            @Override
            public boolean test(Object object) {
                if (object instanceof Objective objective) return objective.isModifiable();
                else if (object instanceof Criteria criteria) return !criteria.isReadOnly();
                throw new IllegalStateException();
            }
        }, isNegated());
    }

    @Override
    public String toString(Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("scores of ", this.objects);
        builder.append(this.isNegated() ? "are not" : "are");
        builder.append("modifiable");
        return builder.toString();
    }

}
