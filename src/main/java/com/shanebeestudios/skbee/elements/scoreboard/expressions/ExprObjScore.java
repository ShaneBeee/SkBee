package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Objective Score")
@Description({"Get/Set the score of an entity/string for an objective.",
    "If the score has never been set, it'll return nothing."})
@Examples({"set score of player for {_objective} to 10",
    "set score of \"le_test\" for {_objective} to 25",
    "set {_score} to score of target entity for {_objective}",
    "set {_score} to score of \"le_test\" for {_objective}"})
@Since("2.6.0")
public class ExprObjScore extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprObjScore.class, Number.class, ExpressionType.COMBINED,
            "score of %entities/strings% for %objective%");
    }

    private Expression<Objective> objective;
    private Expression<?> entries;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entries = exprs[0];
        this.objective = (Expression<Objective>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Number[] get(Event event) {
        List<Number> scores = new ArrayList<>();
        Objective objective = this.objective.getSingle(event);
        if (objective != null) {
            for (Object entry : this.entries.getArray(event)) {
                scores.add(getScore(objective, entry));
            }
        }
        return scores.toArray(new Number[0]);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(Number.class);
            case REMOVE_ALL, RESET, DELETE -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) return;

        if (delta[0] instanceof Number number) {
            int changeValue = number.intValue();
            for (Object entry : this.entries.getArray(event)) {
                Number score = getScore(objective, entry);
                int oldScore = score != null ? score.intValue() : 0;
                switch (mode) {
                    case SET -> setScore(objective, entry, changeValue);
                    case ADD -> setScore(objective, entry, oldScore + changeValue);
                    case REMOVE -> setScore(objective, entry, oldScore - changeValue);
                }
            }
        }
    }

    private Number getScore(Objective objective, Object entry) {
        String stringEntiry = null;
        if (entry instanceof Player player) {
            stringEntiry = player.getName();
        } else if (entry instanceof Entity entity) {
            stringEntiry = entity.getUniqueId().toString();
        } else if (entry instanceof String string) {
            stringEntiry = string;
        }
        if (stringEntiry != null) {
            Score score = objective.getScore(stringEntiry);
            if (score.isScoreSet()) {
                return score.getScore();
            }
        }
        return null;
    }

    private void setScore(Objective objective, Object entry, int score) {
        String stringEntiry = null;
        if (entry instanceof Player player) {
            stringEntiry = player.getName();
        } else if (entry instanceof Entity entity) {
            stringEntiry = entity.getUniqueId().toString();
        } else if (entry instanceof String string) {
            stringEntiry = string;
        }
        if (stringEntiry != null) {
            if (objective.isModifiable()) {
                objective.getScore(stringEntiry).setScore(score);
            } else {
                error("Objective '" + objective.getName() + "' has no modifiable score");
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.entries.isSingle();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "score of " + this.entries.toString(e, d) +
            " for " + this.objective.toString(e, d);
    }

}
