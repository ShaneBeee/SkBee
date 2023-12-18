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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ServerTickManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Server Tick - Tick Rate")
@Description({"Represents the tick rate of the server. Can be a value from `1.0` to `10000.0`.",
        "Default = 20.", Util.MCWIKI_TICK_COMMAND, "Requires Minecraft 1.20.4+"})
@Examples({"set {_rate} to server tick rate",
        "set server tick rate to 100",
        "add 10 to server tick rate",
        "remove 5 from server tick rate",
        "reset server tick rate"})
@Since("INSERT VERSION")
public class ExprServerTickRate extends SimpleExpression<Number> {

    static {
        if (Skript.classExists("org.bukkit.ServerTickManager")) {
            Skript.registerExpression(ExprServerTickRate.class, Number.class, ExpressionType.SIMPLE,
                    "server tick rate");
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Number[] get(Event e) {
        return new Number[]{Bukkit.getServerTickManager().getTickRate()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Number.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ServerTickManager tickManager = Bukkit.getServerTickManager();

        float newValue = 20;
        float previousValue = tickManager.getTickRate();

        if (delta != null && delta[0] instanceof Number num) {
            float changeValue = num.floatValue();
            if (mode == ChangeMode.SET) newValue = changeValue;
            else if (mode == ChangeMode.ADD) newValue = previousValue + changeValue;
            else if (mode == ChangeMode.REMOVE) newValue = previousValue - changeValue;
        }

        // no underscores in ints because Sparky said "but it doesn't look good ever"
        if (newValue >= 1 && newValue <= 10000) tickManager.setTickRate(newValue);
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "server tick rate";
    }

}
