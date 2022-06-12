package com.shanebeestudios.skbee.elements.statistic.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Statistic - Get/Set")
@Description("Represents the statistic of a Player. You can get, set, add, remove or reset stats.")
@Examples({"set {_s} to kill entity stat using sheep for player",
        "set kill entity stat using zombie for player to 10",
        "add 10 to mine block stat using diamond ore for player",
        "remove 10 from chest opened stat for player",
        "reset mob kills stat for player"})
@Since("INSERT VERSION")
public class ExprPlayerStatistic extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprPlayerStatistic.class, Number.class, ExpressionType.COMBINED,
                "%statistic% stat[istic] [using %-entitydata/itemtype%] (of|for) %offlineplayers%");
    }

    private Expression<Statistic> statistic;
    private Expression<OfflinePlayer> player;
    private Expression<Object> qualifier;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.statistic = (Expression<Statistic>) exprs[0];
        this.qualifier = (Expression<Object>) exprs[1];
        this.player = (Expression<OfflinePlayer>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
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

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, SET, REMOVE, RESET -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int change = (delta != null && delta[0] instanceof Number) ? ((Number) delta[0]).intValue() : 0;
        Object qualifier = this.qualifier != null ? this.qualifier.getSingle(event) : null;
        Statistic statistic = this.statistic.getSingle(event);

        if (statistic == null) return;

        for (OfflinePlayer offlinePlayer : this.player.getArray(event)) {
            switch (mode) {
                case ADD -> change += getStat(offlinePlayer, statistic, qualifier);
                case REMOVE -> change = getStat(offlinePlayer, statistic, qualifier) - change;
                case RESET -> change = 0;
            }
            setStat(offlinePlayer, statistic, change, qualifier);
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
