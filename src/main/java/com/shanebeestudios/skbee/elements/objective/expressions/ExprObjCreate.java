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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Name("Scoreboard - Objective Create")
@Description({"Create an objective.",
        "\nID = the id used to keep track of your objective.",
        "\nCRITERIA = uses a criteria object. If using an older server version, this will just be a string.",
        "\nNAMED = the display name of this objective.",
        "\nRENDER TYPE = how this objective will render, hearts or integer.",
        "\nDISPLAY SLOT = where this objective will render."})
@Examples("set {_obj} to objective with id \"le-health\" with criteria {_c} named \"&bLe &cHealth\" with render type hearts in display slot player_list")
@Since("2.6.0")
public class ExprObjCreate extends SimpleExpression<Objective> {

    private static final boolean HAS_CRITERIA_CLASS = Skript.classExists("org.bukkit.scoreboard.Criteria");

    static {
        String pattern = HAS_CRITERIA_CLASS ? "criteria/string" : "string";
        Skript.registerExpression(ExprObjCreate.class, Objective.class, ExpressionType.COMBINED,
                "objective with id %string% with [criteria] %" + pattern + "% named " +
                        "%string% [with render[ ]type %-rendertype%] [(with|in) [display[ ]slot] %-displayslot%]");
    }

    private Expression<String> id;
    private Expression<Criteria> criteria;
    private Expression<String> stringCriteria;
    private Expression<String> displayName;
    private Expression<RenderType> renderType;
    private Expression<DisplaySlot> displaySlot;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        if (HAS_CRITERIA_CLASS) {
            this.criteria = (Expression<Criteria>) exprs[1];
            this.stringCriteria = null;
        } else {
            this.criteria = null;
            this.stringCriteria = (Expression<String>) exprs[1];
        }
        this.displayName = (Expression<String>) exprs[2];
        this.renderType = (Expression<RenderType>) exprs[3];
        this.displaySlot = (Expression<DisplaySlot>) exprs[4];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Override
    protected @Nullable Objective[] get(Event event) {
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String id = this.id.getSingle(event);
        String displayName = this.displayName.getSingle(event);
        Objective objective;
        RenderType renderType;
        if (this.renderType == null) {
            renderType = null;
        } else {
            renderType = this.renderType.getSingle(event);
        }
        if (id == null) return null;

        objective = mainScoreboard.getObjective(id);
        if (objective == null) {
            if (HAS_CRITERIA_CLASS) {
                Criteria criteria = this.criteria.getSingle(event);
                if (displayName != null && criteria != null) {
                    objective = mainScoreboard.registerNewObjective(id, criteria, displayName, Objects.requireNonNullElseGet(renderType, criteria::getDefaultRenderType));
                }
            } else {
                String criteria = this.stringCriteria.getSingle(event);
                if (displayName != null && criteria != null) {
                    if (renderType != null) {
                        objective = mainScoreboard.registerNewObjective(id, criteria, displayName, renderType);
                    } else {
                        objective = mainScoreboard.registerNewObjective(id, criteria, displayName);
                    }
                }
            }
            if (this.displaySlot != null) {
                DisplaySlot displaySlot = this.displaySlot.getSingle(event);
                if (displaySlot != null && objective != null) {
                    objective.setDisplaySlot(displaySlot);
                }
            }
        }
        return new Objective[]{objective};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Objective> getReturnType() {
        return Objective.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String criteria = HAS_CRITERIA_CLASS ? this.criteria.toString(e,d) : this.stringCriteria.toString(e,d);
        return "objective with id " + this.id.toString(e, d) + " with criteria " +
                criteria + " named " + this.displayName.toString(e, d);
    }

}
