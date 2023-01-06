package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Map Marker Color")
@Description({"Represents the map marker colors of a mapping plugin (currently only supports BlueMaps).",
        "If not set, will pull from the default value in the bound config."})
@Examples({"set map marker fill color of {_bound} to rgb(1,165,165)",
        "set map marker line color of {_bound} to black"})
@Since("INSERT VERSION")
public class ExprBoundMapColors extends SimplePropertyExpression<Bound, Color> {

    static {
        register(ExprBoundMapColors.class, Color.class, "map marker (:fill|line) color", "bounds");
    }

    private boolean fill;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.fill = parseResult.hasTag("fill");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Color convert(Bound bound) {
        org.bukkit.Color color;
        if (this.fill) {
            color = bound.getFillColor();
        } else {
            color = bound.getLineColor();
        }
        return SkriptColor.fromBukkitColor(color);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Color.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta[0] instanceof Color color) {
            org.bukkit.Color bukkitColor = color.asBukkitColor();
            for (Bound bound : getExpr().getArray(event)) {
                if (this.fill) {
                    bound.setFillColor(bukkitColor);
                } else {
                    bound.setLineColor(bukkitColor);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        String fill = this.fill ? "fill" : "line";
        return "map marker " + fill + " color";
    }

}
