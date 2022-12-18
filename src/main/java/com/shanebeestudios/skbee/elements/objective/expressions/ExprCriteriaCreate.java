package com.shanebeestudios.skbee.elements.objective.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Criteria;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Criteria Create")
@Description({"Get one of the default Minecraft scoreboard criterias",
        "(see McWiki <link>https://minecraft.fandom.com/wiki/Scoreboard#Criteria</link>) or create your own."})
@Examples("set {_c} to criteria with id \"health\"")
@Since("INSERT VERSION")
public class ExprCriteriaCreate extends SimpleExpression<Criteria> {

    private static final boolean HAS_CRITERIA_CLASS = Skript.classExists("org.bukkit.scoreboard.Criteria");

    static {
        if (HAS_CRITERIA_CLASS) {
            Skript.registerExpression(ExprCriteriaCreate.class, Criteria.class, ExpressionType.COMBINED,
                    "criteria with id %string%");
        }
    }

    private Expression<String> id;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Criteria[] get(Event event) {
        String id = this.id.getSingle(event);
        if (id != null) {
            return new Criteria[]{Bukkit.getScoreboardCriteria(id)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Criteria> getReturnType() {
        return Criteria.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "criteria with id " + this.id.toString(e,d);
    }

}
