package com.shanebeestudios.skbee.elements.objective.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Objective Score")
@Description("Get/Set the score of an entity for an objective.")
@Examples("set score of player for {_objective} to 10")
@Since("INSERT VERSION")
public class ExprObjScore extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprObjScore.class, Number.class, ExpressionType.COMBINED,
                "score of %entities% for %objective%");
    }

    private Expression<Objective> objective;
    private Expression<Entity> entities;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.objective = (Expression<Objective>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Number[] get(Event event) {
        List<Number> scores = new ArrayList<>();
        Objective objective = this.objective.getSingle(event);
        if (objective != null) {
            for (Entity entity : this.entities.getArray(event)) {
                scores.add(getScore(objective, entity));
            }
        }
        return scores.toArray(new Number[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(Number.class);
            case REMOVE_ALL, RESET, DELETE -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) return;

        if (delta[0] instanceof Number number) {
            int changeValue = number.intValue();
            for (Entity entity : this.entities.getArray(event)) {
                int oldScore = getScore(objective, entity);
                switch (mode) {
                    case SET -> setScore(objective, entity, changeValue);
                    case ADD -> setScore(objective, entity, oldScore + changeValue);
                    case REMOVE -> setScore(objective, entity, oldScore - changeValue);
                }
            }
        }
    }

    private int getScore(Objective objective, Entity entity) {
        String entry;
        if (entity instanceof Player player) {
            entry = player.getName();
        } else {
            entry = entity.getUniqueId().toString();
        }
        return objective.getScore(entry).getScore();
    }

    private void setScore(Objective objective, Entity entity, int score) {
        String entry;
        if (entity instanceof Player player) {
            entry = player.getName();
        } else {
            entry = entity.getUniqueId().toString();
        }
        objective.getScore(entry).setScore(score);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "score of entities " + this.entities.toString(e,d) +
                " for objective " + this.objective.toString(e,d);
    }

}
