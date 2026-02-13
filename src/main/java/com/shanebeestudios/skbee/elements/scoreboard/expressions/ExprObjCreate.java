package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ExprObjCreate extends SimpleExpression<Objective> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprObjCreate.class, Objective.class,
                "objective with id %string% with [criteria] %criteria/string% named " +
                    "%string% [(for|of|from) %scoreboard%] [with render[ ]type %-rendertype%] [(with|in) [display[ ]slot] %-displayslot%]")
            .name("Scoreboard - Objective Create")
            .description("Create an objective.",
                "- `id` = The id used to keep track of your objective.",
                "- `criteria` = Uses a criteria object. If using an older server version, this will just be a string.",
                "- `named` = The display name of this objective.",
                "- `for scoreboard` = Optionally use a specific scoreboard (will default to the main scoreboard).",
                "- `render type` = How this objective will render, hearts or integer.",
                "- `display slot` = Where this objective will render.")
            .examples("set {_obj} to objective with id \"le-health\" with criteria {_c} named \"&bLe &cHealth\" with render type hearts in display slot player_list",
                "set {_obj} to objective with id \"my_objective\" with criteria \"dummy\" named \"OUR GOALS!\" for scoreboard of player")
            .since("2.6.0")
            .register();
    }

    private Expression<String> id;
    private Expression<?> criteria;
    private Expression<String> displayName;
    private Expression<Scoreboard> scoreboard;
    private Expression<RenderType> renderType;
    private Expression<DisplaySlot> displaySlot;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        this.criteria = exprs[1];
        this.displayName = (Expression<String>) exprs[2];
        this.scoreboard = (Expression<Scoreboard>) exprs[3];
        this.renderType = (Expression<RenderType>) exprs[4];
        this.displaySlot = (Expression<DisplaySlot>) exprs[5];
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Objective @Nullable [] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) {
            return null;
        }
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

        objective = scoreboard.getObjective(id);
        if (objective == null) {
            Criteria criteria;
            Object object = this.criteria.getSingle(event);
            if (object instanceof Criteria c) criteria = c;
            else if (object instanceof String string) criteria = Criteria.create(string);
            else return null;

            if (displayName != null) {
                objective = scoreboard.registerNewObjective(id, criteria, displayName, Objects.requireNonNullElseGet(renderType, criteria::getDefaultRenderType));
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
    public @NotNull String toString(Event e, boolean d) {
        String render = this.renderType != null ? (" with render type " + this.renderType.toString(e, d)) : "";
        String display = this.displaySlot != null ? (" in display slot " + this.displaySlot.toString(e, d)) : "";
        return "objective with id " + this.id.toString(e, d) + " with criteria " +
            this.criteria.toString(e, d) + " named " + this.displayName.toString(e, d) +
            render + display;
    }

}
