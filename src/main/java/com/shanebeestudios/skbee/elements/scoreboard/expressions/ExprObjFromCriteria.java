package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExprObjFromCriteria extends SimpleExpression<Objective> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprObjFromCriteria.class, Objective.class,
                "objectives (from|by) criteria[s] %criterias% [(of|from) %scoreboards%]")
            .name("Scoreboard - Objective from Criteria")
            .description("Get objectives from specific criterias of scoreboards.")
            .register();
    }

    private Expression<Criteria> criterias;
    private Expression<Scoreboard> scoreboards;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.criterias = (Expression<Criteria>) exprs[0];
        this.scoreboards = (Expression<Scoreboard>) exprs[1];
        return true;
    }

    @Override
    protected Objective @Nullable [] get(Event event) {
        List<Objective> objectives = new ArrayList<>();

        for (Scoreboard scoreboard : this.scoreboards.getArray(event)) {
            for (Criteria criteria : this.criterias.getArray(event)) {
                Set<Objective> objectivesByCriteria = scoreboard.getObjectivesByCriteria(criteria);
                objectives.addAll(objectivesByCriteria);
            }

        }
        return objectives.toArray(new Objective[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Objective> getReturnType() {
        return Objective.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return new SyntaxStringBuilder(event, debug)
            .append("objectives by criteria", this.criterias)
            .append("from", this.scoreboards)
            .toString();
    }

}
