package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Time;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Player Time")
@Description({"Represents the current time on the player's client.",
        "When relative is used the player's time will be kept synchronized to its world time with the specified offset.",
        "When using non-relative time the player's time will stay fixed at the specified time parameter.",
        "It's up to the caller to continue updating the player's time.",
        "To restore player time to normal use reset.",
        "Both Time and TimeSpan can be used for this. It is best to use Time for non-relative and TimeSpan for relative.",
        "Relative will return as a TimeSpan (the offset from world time), non-relative will return as a Time."})
@Examples({"set player time of player to 12:00am",
        "set player time of all players to 6pm",
        "set relative player time of player to 12000 ticks",
        "set relative player time of player to 10 minutes",
        "set relative player time of all players to 6000 ticks",
        "add 10 minutes to player time of player",
        "add 1 minute to relative player time of player",
        "remove 10 minutes from player time of all players",
        "remove 1 minute from relative player time of player",
        "reset player time of player",
        "reset player time of all players"})
@Since("INSERT VERSION")
public class ExprPlayerTime extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprPlayerTime.class, Object.class, ExpressionType.SIMPLE,
                "[:relative] (player|client) time of %players%");
    }

    private boolean relative;
    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.relative = parseResult.hasTag("relative");
        this.players = (Expression<Player>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        List<Object> times = new ArrayList<>();
        for (Player player : this.players.getArray(event)) {
            if (this.relative) {
                times.add(Timespan.fromTicks_i(player.getPlayerTimeOffset()));
            } else {
                times.add(new Time((int) player.getPlayerTime()));
            }
        }
        return times.toArray(new Object[0]);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE) {
            return CollectionUtils.array(Time.class, Timespan.class);
        } else if (mode == ChangeMode.RESET) {
            return CollectionUtils.array();
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.RESET) {
            for (Player player : this.players.getArray(event)) {
                player.resetPlayerTime();
            }
        } else if (delta != null) {
            int ticks = 0;
            if (delta[0] instanceof Timespan timespan) {
                ticks = (int) timespan.getTicks_i();
            } else if (delta[0] instanceof Time time) {
                ticks = time.getTicks();
            }
            for (Player player : this.players.getArray(event)) {
                int value = getValue(mode, player, ticks);
                player.setPlayerTime(value, this.relative);
            }
        }
    }

    private int getValue(ChangeMode mode, Player player, int ticks) {
        int value = 0;
        if (mode == ChangeMode.ADD) {
            if (this.relative) value = (int) player.getPlayerTimeOffset() + ticks;
            else value = (int) player.getPlayerTime() + ticks;
        } else if (mode == ChangeMode.REMOVE) {
            if (this.relative) value = (int) player.getPlayerTimeOffset() - ticks;
            else value = (int) player.getPlayerTime() - ticks;
        } else if (mode == ChangeMode.SET) {
            value = ticks;
        }
        return value;
    }

    @Override
    public boolean isSingle() {
        return this.players.isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (this.relative) return Timespan.class;
        return Time.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String relative = this.relative ? "relative " : "";
        return relative + "player time of " + this.players.toString(e, d);
    }

}
