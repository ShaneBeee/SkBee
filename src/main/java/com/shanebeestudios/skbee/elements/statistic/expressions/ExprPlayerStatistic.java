package com.shanebeestudios.skbee.elements.statistic.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprPlayerStatistic extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprPlayerStatistic.class, Number.class,
                "%statistic% stat[istic] [using %-entitydata/itemtype%] (of|for) %offlineplayers%")
            .name("Statistic - Get/Set")
            .description("Represents the statistic of a Player. You can get, set, add, remove or reset stats.")
            .examples("set {_s} to kill entity stat using sheep for player",
                "set kill entity stat using zombie for player to 10",
                "add 10 to mine block stat using diamond ore for player",
                "remove 10 from chest opened stat for player",
                "reset mob kills stat for player")
            .since("1.17.0")
            .register();
    }

    private Expression<Statistic> statistic;
    private Expression<OfflinePlayer> player;
    private Expression<Object> qualifier;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.statistic = (Expression<Statistic>) exprs[0];
        this.qualifier = (Expression<Object>) exprs[1];
        this.player = (Expression<OfflinePlayer>) exprs[2];
        return true;
    }

    @Override
    protected @Nullable Number[] get(Event event) {
        List<Number> stats = new ArrayList<>();
        Statistic statistic = this.statistic.getSingle(event);
        Object qualifier = this.qualifier != null ? this.qualifier.getSingle(event) : null;
        if (statistic == null) return null;

        for (OfflinePlayer offlinePlayer : this.player.getArray(event)) {
            stats.add(getStat(offlinePlayer, statistic, qualifier));
        }
        return stats.toArray(new Number[0]);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, SET, REMOVE, RESET -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int change = (delta != null && delta[0] instanceof Number) ? ((Number) delta[0]).intValue() : 0;
        Object qualifier = this.qualifier != null ? this.qualifier.getSingle(event) : null;
        Statistic statistic = this.statistic.getSingle(event);

        if (statistic == null) return;

        for (OfflinePlayer offlinePlayer : this.player.getArray(event)) {
            int changeValue = change;
            switch (mode) {
                case ADD -> changeValue += getStat(offlinePlayer, statistic, qualifier);
                case REMOVE -> changeValue = getStat(offlinePlayer, statistic, qualifier) - changeValue;
                case RESET -> changeValue = 0;
            }
            setStat(offlinePlayer, statistic, changeValue, qualifier);
        }
    }

    private void setStat(OfflinePlayer player, Statistic statistic, int change, Object qualifier) {
        if (change < 0) {
            change = 0;
        }
        Type statType = statistic.getType();
        if (statType == Type.UNTYPED) {
            player.setStatistic(statistic, change);
        } else if (statType == Type.ENTITY && qualifier instanceof EntityData<?> entityData) {
            EntityType entityType = EntityUtils.toBukkitEntityType(entityData);
            player.setStatistic(statistic, entityType, change);
        } else if (statType == Type.BLOCK && qualifier instanceof ItemType itemType) {
            Material material = itemType.getMaterial();
            if (material.isBlock()) {
                player.setStatistic(statistic, material, change);
            }
        } else if (statType == Type.ITEM && qualifier instanceof ItemType itemType) {
            Material material = itemType.getMaterial();
            if (material.isItem()) {
                player.setStatistic(statistic, material, change);
            }
        }
    }

    private int getStat(OfflinePlayer player, Statistic statistic, Object qualifier) {
        Type statType = statistic.getType();
        if (statType == Type.UNTYPED) {
            return player.getStatistic(statistic);
        } else if (statType == Type.ENTITY && qualifier instanceof EntityData<?> entityData) {
            EntityType entityType = EntityUtils.toBukkitEntityType(entityData);
            return player.getStatistic(statistic, entityType);
        } else if (statType == Type.BLOCK && qualifier instanceof ItemType itemType) {
            Material material = itemType.getMaterial();
            if (material.isBlock()) {
                return player.getStatistic(statistic, material);
            }
        } else if (statType == Type.ITEM && qualifier instanceof ItemType itemType) {
            Material material = itemType.getMaterial();
            if (material.isItem()) {
                return player.getStatistic(statistic, material);
            }
        }
        return 0;
    }

    @Override
    public boolean isSingle() {
        return this.player.isSingle();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String qualifier = this.qualifier != null ? "using " + this.qualifier.toString(e, d) : "";
        return this.statistic.toString(e, d) + " statistic " + qualifier + " of " + this.player.toString(e, d);
    }

}
