package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Death Location of Player")
@Description({"Represents the last death location of player.",
    "Set/Delete will only work for online players not offline players.",
    "Removed if running Skript 2.10+ (now included in Skript)."})
@Examples({"command /death:",
    "\ttrigger:",
    "\t\tteleport player to last death location of player"})
@Since("2.8.5")
public class ExprLastDeathLocation extends SimplePropertyExpression<OfflinePlayer, Location> {

    static {
        if (!Util.IS_RUNNING_SKRIPT_2_10) {
            register(ExprLastDeathLocation.class, Location.class, "[last ]death location", "offlineplayers");
        }
    }

    @Override
    public @Nullable Location convert(OfflinePlayer offlinePlayer) {
        if (offlinePlayer instanceof Player player) return player.getLastDeathLocation();
        return offlinePlayer.getLastDeathLocation();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) return CollectionUtils.array(Location.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Location location = null;
        if (delta != null && delta[0] instanceof Location) {
            location = (Location) delta[0];
        }
        for (OfflinePlayer offlinePlayer : getExpr().getArray(event)) {
            if (offlinePlayer instanceof Player player) {
                player.setLastDeathLocation(location);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "last death location";
    }

}
