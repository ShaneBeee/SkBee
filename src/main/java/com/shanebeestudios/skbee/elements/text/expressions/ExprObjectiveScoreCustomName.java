package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Objective Score Custom Name")
@Description({"Get/change the custom name of the score of an objective for an entry.",
    "Supports both components and strings.",
    "Requires Paper 1.20.4+",
    "- `[component]` = Will return as a text component (for set this doesn't matter).",
    "- `delete` = Will set the name to an empty component.",
    "- `reset` = Will reset back to the original name."})
@Examples({"set component custom score name of player for {_someObjective} to mini message from \"Some Name\"",
    "set custom score name of player for {_someObjective} to \"Some Name\"",
    "delete custom score name of \"bob\" for {_someObjective}",
    "reset custom score name of all entities for {_someObjective}"})
@Since("3.9.0")
public class ExprObjectiveScoreCustomName extends SimpleExpression<Object> {

    static {
        if (SkBee.getPlugin().getPluginConfig().ELEMENTS_SCOREBOARD && Skript.methodExists(Score.class, "customName")) {
            Skript.registerExpression(ExprObjectiveScoreCustomName.class, Object.class, ExpressionType.COMBINED,
                "[:component] custom score name of %entities/strings% for %objective%");
        }
    }

    private boolean isComponent;
    private Expression<?> entries;
    private Expression<Objective> objective;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.isComponent = parseResult.hasTag("component");
        this.entries = exprs[0];
        this.objective = (Expression<Objective>) exprs[1];
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) {
            error("Missing objective: " + this.objective.toString(event, true));
            return null;
        }
        List<Object> comps = new ArrayList<>();
        for (Object object : this.entries.getArray(event)) {
            Score score;
            if (object instanceof Entity entity) score = objective.getScoreFor(entity);
            else if (object instanceof String string) score = objective.getScore(string);
            else continue; // This shouldn't happen

            Component component = score.customName();
            if (component == null) continue;
            ComponentWrapper cw = ComponentWrapper.fromComponent(component);
            comps.add(this.isComponent ? cw : cw.toString());
        }

        return comps.toArray(this.isComponent ? new ComponentWrapper[0] : new String[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class, String.class);
        else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) {
            error("Missing objective: " + this.objective.toString(event, true));
            return;
        }

        Component component = null;
        if (delta != null) {
            if (delta[0] instanceof ComponentWrapper cw) component = cw.getComponent();
            else if (delta[0] instanceof String string) component = Component.text(string);
        }
        for (Object object : this.entries.getArray(event)) {
            Score score;
            if (object instanceof Entity entity) score = objective.getScoreFor(entity);
            else if (object instanceof String string) score = objective.getScore(string);
            else continue; // This shouldn't happen

            if (mode == ChangeMode.SET) {
                if (component == null) {
                    error("Missing component: " + this.objective.toString(event, true));
                    continue;
                }
                score.customName(component);
            } else if (mode == ChangeMode.DELETE) {
                score.customName(Component.empty());
            } else {
                score.customName(component);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.entries.isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        return this.isComponent ? ComponentWrapper.class : String.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return new SyntaxStringBuilder(e, d)
            .append(this.isComponent ? "component" : "")
            .append("custom score name of", this.entries)
            .append("for", this.objective)
            .toString();
    }

}
