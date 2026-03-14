package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprEntityWaypointColor extends SimplePropertyExpression<LivingEntity, Color> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityWaypointColor.class, Color.class,
                "waypoint color", "livingentities")
            .name("Entity Waypoint Color")
            .description("Get/set/delete the waypoint color of an entity.")
            .examples("set {_color} to waypoint color of player",
                "set waypoint color of all players to red",
                "set waypoint color of player to rgb(10, 200, 150)",
                "reset waypoint color of target entity")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Color convert(LivingEntity from) {
        SkriptColor skriptColor = SkriptColor.fromBukkitColor(from.getWaypointColor());
        if (skriptColor != null) return skriptColor;
        return ColorRGB.fromBukkitColor(from.getWaypointColor());
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Color.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        org.bukkit.Color color = null;
        if (delta != null && delta.length > 0 && delta[0] instanceof Color skriptcolor) {
            color = skriptcolor.asBukkitColor();
        }

        for (LivingEntity entity : getExpr().getArray(event)) {
            entity.setWaypointColor(color);
        }
    }

    @Override
    protected String getPropertyName() {
        return "waypoint color";
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

}
