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
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Scoreboard - Objective from DisplaySlot")
@Description("Get/clear objectives from specific display slots of scoreboards.")
@Examples({"set {_obj} to objectives from slot sidebar of scoreboard of player",
    "set {_obj} to objectives from slot player list",
    "set {_obj} to objectives from slot below name from main scoreboard",
    "clear objectives from slot player list of main scoreboard"})
@Since("INSERT VERSION")
public class ExprObjFromDisplaySlot extends SimpleExpression<Objective> {

    static {
        Skript.registerExpression(ExprObjFromDisplaySlot.class, Objective.class, ExpressionType.COMBINED,
            "objective[s] (from|by) [display[ ]]slot[s] %displayslots% [(of|from) %scoreboards%]");
    }

    private Expression<DisplaySlot> displaySlots;
    private Expression<Scoreboard> scoreboards;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.displaySlots = (Expression<DisplaySlot>) exprs[0];
        this.scoreboards = (Expression<Scoreboard>) exprs[1];
        return true;
    }

    @Override
    protected Objective @Nullable [] get(Event event) {
        List<Objective> objectives = new ArrayList<>();
        for (Scoreboard scoreboard : this.scoreboards.getArray(event)) {
            for (DisplaySlot displaySlot : this.displaySlots.getArray(event)) {
                Objective objective = scoreboard.getObjective(displaySlot);
                if (objective != null) objectives.add(objective);
            }

        }
        return objectives.toArray(new Objective[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        for (Scoreboard scoreboard : this.scoreboards.getArray(event)) {
            for (DisplaySlot displaySlot : this.displaySlots.getArray(event)) {
                scoreboard.clearSlot(displaySlot);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.scoreboards.isSingle() && this.displaySlots.isSingle();
    }

    @Override
    public Class<? extends Objective> getReturnType() {
        return Objective.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return new SyntaxStringBuilder(event, debug)
            .append("objectives from slot", this.displaySlots)
            .append("of", this.scoreboards)
            .toString();
    }

}
