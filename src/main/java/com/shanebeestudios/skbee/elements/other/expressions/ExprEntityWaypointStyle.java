package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprEntityWaypointStyle extends SimplePropertyExpression<LivingEntity, NamespacedKey> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityWaypointStyle.class, NamespacedKey.class,
                "waypoint style", "livingentities")
            .name("Entity Waypoint Style")
            .description("Get/set/delete the waypoint style of an entity.",
                "Can be set to a NamespacedKey or a string.",
                "See [**Waypoint Style**](https://minecraft.wiki/w/Waypoint_style) on McWiki for more info.")
            .examples("set {_key} to waypoint style of player",
                "set waypoint style of target entity to \"minecraft:bowtie\"",
                "reset waypoint style of all players")
            .since("3.18.0")
            .register();
    }

    @Override
    public @Nullable NamespacedKey convert(LivingEntity from) {
        return NamespacedKey.fromString(from.getWaypointStyle().asString());
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET) {
            return CollectionUtils.array(NamespacedKey.class, String.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        NamespacedKey key = null;
        if (delta != null && delta.length > 0) {
            if (delta[0] instanceof NamespacedKey nsk) {
                key = nsk;
            } else if (delta[0] instanceof String str) {
                key = Util.getNamespacedKey(str, false);
            }
        }

        for (LivingEntity entity : getExpr().getArray(event)) {
            entity.setWaypointStyle(key);
        }
    }

    @Override
    protected String getPropertyName() {
        return "waypoint style";
    }

    @Override
    public Class<? extends NamespacedKey> getReturnType() {
        return NamespacedKey.class;
    }

}
