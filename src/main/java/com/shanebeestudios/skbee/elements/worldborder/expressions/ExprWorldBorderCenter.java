package com.shanebeestudios.skbee.elements.worldborder.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("WorldBorder - Center")
@Description("Get/set the center location of a world border.")
@Examples("set center of world border of player to location(1,1,1)")
@Since("1.17.0")
public class ExprWorldBorderCenter extends SimplePropertyExpression<WorldBorder, Location> {

    static {
        register(ExprWorldBorderCenter.class, Location.class, "[border] center", "worldborders");
    }

    @Override
    public @Nullable Location convert(WorldBorder worldBorder) {
        return worldBorder.getCenter();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Location.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Location location = (Location) delta[0];
        if (location == null) return;

        for (WorldBorder border : getExpr().getArray(event)) {
            border.setCenter(location);
        }
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "world border center";
    }

}
