package com.shanebeestudios.skbee.elements.worldborder.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("WorldBorder - Warning Time")
@Description("Get/set the warning time of a world border.")
@Examples("set warning time of world border of world \"world\" to 5 seconds")
@Since("1.17.0")
public class ExprWorldBorderWarningTime extends SimplePropertyExpression<WorldBorder, Timespan> {

    static {
        register(ExprWorldBorderWarningTime.class, Timespan.class,
                "[border] warning time", "worldborders");
    }

    @Override
    public @Nullable Timespan convert(WorldBorder worldBorder) {
        return Timespan.fromTicks_i(worldBorder.getWarningTime());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Timespan.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Timespan timespan = (Timespan) delta[0];
        if (timespan == null) return;

        int seconds = (int)(timespan.getTicks_i() / 20);
        for (WorldBorder border : getExpr().getArray(event)) {
            border.setWarningTime(seconds);
        }
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "warning time";
    }

}
